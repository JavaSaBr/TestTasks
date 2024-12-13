package javasabr.device.rest.handler;

import static javasabr.device.rest.DeviceRestConstants.PV_MAC_ADDRESS;

import java.util.List;
import javasabr.device.model.NetworkDevice;
import javasabr.device.service.DeviceService;
import javasabr.device.rest.DeviceFieldsValidator;
import javasabr.device.rest.dto.NetworkDeviceDto;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class DeviceDiscoveryHandler {

  private final @NotNull DeviceService deviceService;
  private final @NotNull DeviceFieldsValidator deviceFieldsValidator;

  public @NotNull Mono<ServerResponse> getAllRegisteredDevices(@NotNull ServerRequest request) {

    List<NetworkDeviceDto> result = deviceService
        .getAllDevices()
        .stream()
        .map(NetworkDeviceDto::from)
        .toList();

    return ServerResponse
        .ok()
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(result);
  }

  public @NotNull Mono<ServerResponse> getRegisteredDevice(@NotNull ServerRequest request) {

    String rawMacAddress = request.pathVariable(PV_MAC_ADDRESS);
    String macAddress = deviceFieldsValidator.validateAndNormalizeMacAddress(rawMacAddress);
    NetworkDevice networkDevice = deviceService.getByMacAddress(macAddress);

    if (networkDevice == null) {
      return ServerResponse
          .notFound()
          .build();
    }

    return ServerResponse
        .ok()
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(NetworkDeviceDto.from(networkDevice));
  }

  public @NotNull Mono<ServerResponse> getFullTopology(@NotNull ServerRequest request) {
    return null;
  }

  public @NotNull Mono<ServerResponse> getDeviceTopology(@NotNull ServerRequest request) {
    return null;
  }
}
