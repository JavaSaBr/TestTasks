package javasabr.device.rest.dto;

import javasabr.device.model.NetworkDevice;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record NetworkDeviceDto(
    @NotNull String macAddress,
    @NotNull String type,
    @Nullable String uplinkMacAddress) {

  public static @NotNull NetworkDeviceDto from(@NotNull NetworkDevice networkDevice) {
    var deviceType = networkDevice.type();
    return new NetworkDeviceDto(networkDevice.macAddress(), deviceType.label(), networkDevice.uplinkMacAddress());
  }
}
