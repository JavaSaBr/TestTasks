package javasabr.device.model;

import java.util.Comparator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
public abstract class NetworkDevice implements Comparable<NetworkDevice> {

  private static final Comparator<NetworkDevice> BASE_COMPARATOR = Comparator
      .<NetworkDevice, Integer>comparing(device -> device.type().ordinal())
      .thenComparing(NetworkDevice::macAddress);

  private final @NotNull String macAddress;
  private final @Nullable String uplinkMacAddress;

  public abstract @NotNull NetworkDeviceType type();

  public boolean canConnectTo(@NotNull NetworkDevice uplink) {
    return true;
  }

  @Override
  public int compareTo(@NotNull NetworkDevice another) {
    return BASE_COMPARATOR.compare(this, another);
  }
}
