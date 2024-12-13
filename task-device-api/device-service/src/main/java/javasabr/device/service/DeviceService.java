package javasabr.device.service;

import javasabr.device.model.NetworkDevice;
import javasabr.device.model.NetworkDeviceType;
import javasabr.device.service.exception.DeviceIsAlreadyRegisteredException;
import javasabr.device.service.exception.UplinkDeviceNotFoundException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface DeviceService {

  /**
   * @throws DeviceIsAlreadyRegisteredException if the device with same MAC is already registered
   * @throws UplinkDeviceNotFoundException if uplink device does not exist
   */
  @NotNull NetworkDevice register(
      @NotNull NetworkDeviceType type,
      @NotNull String macAddress,
      @Nullable String uplinkMacAddress) throws DeviceIsAlreadyRegisteredException, UplinkDeviceNotFoundException;

  @Nullable NetworkDevice getByMacAddress(@NotNull String macAddress);
}
