package javasabr.device.service.impl;

import static java.util.concurrent.ConcurrentHashMap.newKeySet;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;
import javasabr.device.model.AccessPointNetworkDevice;
import javasabr.device.model.GatewayNetworkDevice;
import javasabr.device.model.NetworkDevice;
import javasabr.device.model.NetworkDeviceType;
import javasabr.device.model.SwitchNetworkDevice;
import javasabr.device.model.TopologyNode;
import javasabr.device.model.TopologyTree;
import javasabr.device.service.DeviceService;
import javasabr.device.service.exception.DeviceIsAlreadyRegisteredException;
import javasabr.device.service.exception.UnsupportedUplinkException;
import javasabr.device.service.exception.UplinkDeviceNotFoundException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class InMemoryDeviceService implements DeviceService {

  private final @NotNull ConcurrentMap<String, NetworkDevice> registeredDevices = new ConcurrentHashMap<>();
  private final @NotNull ConcurrentMap<String, Set<NetworkDevice>> connectedDevices = new ConcurrentHashMap<>();

  @Override
  public @NotNull NetworkDevice register(
      @NotNull NetworkDeviceType type,
      @NotNull String macAddress,
      @Nullable String uplinkMacAddress) {

    if (registeredDevices.containsKey(macAddress)) {
      throw new DeviceIsAlreadyRegisteredException("Device:[" + macAddress + "] is already registered.");
    }

    NetworkDevice uplinkDevice = uplinkMacAddress == null ? null : registeredDevices.get(uplinkMacAddress);

    if (uplinkMacAddress != null && uplinkDevice == null) {
      throw new UplinkDeviceNotFoundException("Uplink device:[" + uplinkMacAddress + "] is not found.");
    }

    NetworkDevice newDevice = buildDevice(type, macAddress, uplinkMacAddress);

    if (uplinkDevice != null && !newDevice.canConnectTo(uplinkDevice)) {
      throw new UnsupportedUplinkException("Device:[" + macAddress + "] cannot connect to [" + uplinkMacAddress + ".");
    }

    NetworkDevice alreadyRegistered = registeredDevices.putIfAbsent(macAddress, newDevice);

    if (alreadyRegistered != null) {
      throw new DeviceIsAlreadyRegisteredException("Device:[" + macAddress + "] is already registered.");
    }

    if (uplinkDevice != null) {
      connectedDevices
          .computeIfAbsent(uplinkMacAddress, key -> newKeySet())
          .add(newDevice);
    }

    return newDevice;
  }

  @Override
  public @Nullable NetworkDevice getByMacAddress(@NotNull String macAddress) {
    return registeredDevices.get(macAddress);
  }

  @Override
  public @NotNull SortedSet<NetworkDevice> getAllDevices() {
    return new TreeSet<>(registeredDevices.values());
  }

  @Override
  public @Nullable TopologyNode buildDeviceTopology(@NotNull String macAddress) {

    NetworkDevice device = registeredDevices.get(macAddress);
    if (device == null) {
      return null;
    }

    return buildTopology(device);
  }

  @Override
  public @NotNull TopologyTree buildFullTopology() {

    Set<TopologyNode> rootDevices = registeredDevices
        .values()
        .stream()
        .filter(device -> device.uplinkMacAddress() == null)
        .map(this::buildTopology)
        .sorted(Comparator.comparingInt(TopologyNode::totalChildrenCount))
        .collect(Collectors.toCollection(LinkedHashSet::new));

    return new TopologyTree(rootDevices);
  }

  private @NotNull TopologyNode buildTopology(@NotNull NetworkDevice device) {

    Set<NetworkDevice> connected = connectedDevices.get(device.macAddress());

    if (connected == null || connected.isEmpty()) {
      return TopologyNode.of(device, Set.of());
    }

    Set<TopologyNode> children = connected
        .stream()
        .map(this::buildTopology)
        .sorted(Comparator.comparingInt(TopologyNode::totalChildrenCount))
        .collect(Collectors.toCollection(LinkedHashSet::new));

    return TopologyNode.of(device, children);
  }

  private @NotNull NetworkDevice buildDevice(
      @NotNull NetworkDeviceType type,
      @NotNull String macAddress,
      @Nullable String uplinkMacAddress) {
    return switch (type) {
      case SWITCH -> new SwitchNetworkDevice(macAddress, uplinkMacAddress);
      case ACCESS_POINT -> new AccessPointNetworkDevice(macAddress, uplinkMacAddress);
      case GATEWAY -> new GatewayNetworkDevice(macAddress, uplinkMacAddress);
    };
  }
}
