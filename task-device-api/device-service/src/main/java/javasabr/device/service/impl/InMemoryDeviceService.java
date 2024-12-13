package javasabr.device.service.impl;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javasabr.device.model.AccessPointNetworkDevice;
import javasabr.device.model.GatewayNetworkDevice;
import javasabr.device.model.NetworkDevice;
import javasabr.device.model.NetworkDeviceType;
import javasabr.device.model.SwitchNetworkDevice;
import javasabr.device.service.DeviceService;
import javasabr.device.service.exception.DeviceIsAlreadyRegisteredException;
import javasabr.device.service.exception.UnupportedUplinkException;
import javasabr.device.service.exception.UplinkDeviceNotFoundException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class InMemoryDeviceService implements DeviceService {

  private final @NotNull ConcurrentMap<String, NetworkDevice> registeredDevices = new ConcurrentHashMap<>();

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
      throw new UnupportedUplinkException("Device:[" + macAddress + "] cannot connect to [" + uplinkMacAddress + ".");
    }

    NetworkDevice alreadyRegistered = registeredDevices.putIfAbsent(macAddress, newDevice);

    if (alreadyRegistered != null) {
      throw new DeviceIsAlreadyRegisteredException("Device:[" + macAddress + "] is already registered.");
    }

    return newDevice;
  }

  @Override
  public @Nullable NetworkDevice getByMacAddress(@NotNull String macAddress) {
    return registeredDevices.get(macAddress);
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
