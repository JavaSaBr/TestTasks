package javasabr.device.rest;

import inet.ipaddr.MACAddressString;
import javasabr.device.rest.exception.BadRequestException;
import org.jetbrains.annotations.NotNull;

public class DeviceFieldsValidator {

  public @NotNull String validateAndNormalizeMacAddress(@NotNull String rawMacAddress) {
    var macAddressString = new MACAddressString(rawMacAddress);
    if (!macAddressString.isValid()) {
      throw new BadRequestException("MAC address: '%s' is not valid.".formatted(rawMacAddress));
    }
    return macAddressString.toNormalizedString();
  }
}
