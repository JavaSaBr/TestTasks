package javasabr.device.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SwitchNetworkDevice extends NetworkDevice {

  public SwitchNetworkDevice(@NotNull String macAddress, @Nullable String uplinkMacAddress) {
    super(macAddress, uplinkMacAddress);
  }

  @Override
  public @NotNull NetworkDeviceType type() {
    return NetworkDeviceType.SWITCH;
  }
}
