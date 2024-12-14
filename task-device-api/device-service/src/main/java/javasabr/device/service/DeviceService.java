package javasabr.device.service;

import java.util.SortedSet;
import javasabr.device.model.NetworkDevice;
import javasabr.device.model.NetworkDeviceType;
import javasabr.device.model.TopologyNode;
import javasabr.device.model.TopologyTree;
import javasabr.device.service.exception.DeviceIsAlreadyRegisteredException;
import javasabr.device.service.exception.UnsupportedUplinkException;
import javasabr.device.service.exception.UplinkDeviceNotFoundException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface DeviceService {

  /**
   * @throws DeviceIsAlreadyRegisteredException if the device with same MAC is already registered
   * @throws UplinkDeviceNotFoundException if uplink device does not exist
   * @throws UnsupportedUplinkException if device cannot connect to uplink
   */
  @NotNull NetworkDevice register(
      @NotNull NetworkDeviceType type,
      @NotNull String macAddress,
      @Nullable String uplinkMacAddress) throws DeviceIsAlreadyRegisteredException, UplinkDeviceNotFoundException;

  /**
   * @return null is such device does not exist
   */
  @Nullable NetworkDevice getByMacAddress(@NotNull String macAddress);

  @NotNull SortedSet<NetworkDevice> getAllDevices();

  @NotNull TopologyTree buildFullTopology();

  /**
   * @return null is such device does not exist
   */
  @Nullable TopologyNode buildDeviceTopology(@NotNull String macAddress);
}
