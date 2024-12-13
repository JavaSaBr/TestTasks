package javasabr.device.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
public abstract class NetworkDevice {

  private final @NotNull String macAddress;
  private final @Nullable String uplinkMacAddress;

  public abstract @NotNull NetworkDeviceType type();

  public boolean canConnectTo(@NotNull NetworkDevice uplink) {
    return true;
  }
}
