package javasabr.device.rest.handler;

import static javasabr.device.rest.DeviceRestConstants.PV_DEVICE_TYPE;
import static javasabr.device.rest.DeviceRestConstants.PV_MAC_ADDRESS;
import static javasabr.device.rest.DeviceRestConstants.PV_UPLINK_MAC_ADDRESS;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import javasabr.device.model.NetworkDevice;
import javasabr.device.model.NetworkDeviceType;
import javasabr.device.service.DeviceService;
import javasabr.device.service.exception.DeviceIsAlreadyRegisteredException;
import javasabr.device.rest.DeviceFieldsValidator;
import javasabr.device.rest.DeviceRestConstants;
import javasabr.device.rest.exception.BadRequestException;
import javasabr.device.rest.exception.ConflictException;
import javasabr.device.service.exception.UnupportedUplinkException;
import javasabr.device.service.exception.UplinkDeviceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class DeviceRegisterHandler {

  private final @NotNull DeviceService deviceService;
  private final @NotNull DeviceFieldsValidator deviceFieldsValidator;

  public @NotNull Mono<ServerResponse> register(@NotNull ServerRequest request) {
    String rawMacAddress = request.pathVariable(PV_MAC_ADDRESS);
    String rawDeviceType = request.pathVariable(PV_DEVICE_TYPE);
    return register(rawDeviceType, rawMacAddress, null);
  }

  public @NotNull Mono<ServerResponse> registerConnected(@NotNull ServerRequest request) {
    String rawMacAddress = request.pathVariable(PV_MAC_ADDRESS);
    String rawUplinkAddress = request.pathVariable(PV_UPLINK_MAC_ADDRESS);
    String rawDeviceType = request.pathVariable(PV_DEVICE_TYPE);
    return register(rawDeviceType, rawMacAddress, rawUplinkAddress);
  }

  private @NotNull Mono<ServerResponse> register(
      @NotNull String rawDeviceType,
      @NotNull String rawMacAddress,
      @Nullable String rawUplinkMacAddress) {

    var deviceType = NetworkDeviceType.fromLabel(rawDeviceType);
    if (deviceType == null) {
      return Mono.error(new BadRequestException("Device type: '%s' is not valid.".formatted(rawDeviceType)));
    }

    String macAddress = deviceFieldsValidator.validateAndNormalizeMacAddress(rawMacAddress);
    String uplinkMacAddress = null;

    if (rawUplinkMacAddress != null) {
      uplinkMacAddress = deviceFieldsValidator.validateAndNormalizeMacAddress(rawUplinkMacAddress);
    }

    NetworkDevice networkDevice;
    try {
      networkDevice = deviceService.register(deviceType, macAddress, uplinkMacAddress);
    } catch (DeviceIsAlreadyRegisteredException e) {
      return Mono.error(new ConflictException("Device: '%s' is already registered.".formatted(macAddress)));
    } catch (UplinkDeviceNotFoundException e) {
      return Mono.error(new BadRequestException("Uplink device: '%s' is not found.".formatted(uplinkMacAddress)));
    } catch (UnupportedUplinkException e) {
      return Mono.error(new BadRequestException("Uplink device: '%s' is not supported.".formatted(uplinkMacAddress)));
    }

    return ServerResponse
        .created(buildDeviceLocation(networkDevice))
        .build();
  }

  private static @NotNull URI buildDeviceLocation(@NotNull NetworkDevice networkDevice) {
    return URI.create("/%s/%s".formatted(
        DeviceRestConstants.R_DEVICE,
        URLEncoder.encode(networkDevice.macAddress(), StandardCharsets.UTF_8)));
  }
}
