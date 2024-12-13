package javasabr.device.rest.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import javasabr.device.model.NetworkDevice;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
public class NetworkDeviceDto {

  public static @NotNull NetworkDeviceDto from(@NotNull NetworkDevice networkDevice) {
    var deviceType = networkDevice.type();
    return new NetworkDeviceDto(
        networkDevice.macAddress(),
        deviceType.label(),
        networkDevice.uplinkMacAddress());
  }

  private final @NotNull String macAddress;
  private final @NotNull String type;
  private final @Nullable String uplinkMacAddress;

  @JsonCreator
  public NetworkDeviceDto(@NotNull String macAddress, @NotNull String type, @Nullable String uplinkMacAddress) {
    this.macAddress = macAddress;
    this.type = type;
    this.uplinkMacAddress = uplinkMacAddress;
  }
}
