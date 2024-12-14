package javasabr.device.rest.config;

import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

import javasabr.device.rest.handler.DeviceDiscoveryHandler;
import javasabr.device.rest.handler.DeviceRegisterHandler;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Import(DeviceRestHandlersConfig.class)
@Configuration(proxyBeanMethods = false)
public class DeviceRestRoutingConfig {

  @Bean
  RouterFunction<ServerResponse> deviceRouterFunction(
      @NotNull DeviceRegisterHandler deviceRegisterHandler,
      @NotNull DeviceDiscoveryHandler deviceDiscoveryHandler) {
    return RouterFunctions
        .route()
        .path("/device", device -> device
            .path("{deviceType}/{macAddress}", deviceTypeAndMac -> deviceTypeAndMac
                .POST("", deviceRegisterHandler::register)
                .POST("/", deviceRegisterHandler::register)
                .POST("/{uplinkMacAddress}", deviceRegisterHandler::registerConnected)
                .POST("/{uplinkMacAddress}/", deviceRegisterHandler::registerConnected))
            .GET("/{macAddress}", accept(MediaType.APPLICATION_JSON), deviceDiscoveryHandler::getRegisteredDevice)
            .GET("/{macAddress}/", accept(MediaType.APPLICATION_JSON), deviceDiscoveryHandler::getRegisteredDevice))
        .GET("/devices", accept(MediaType.APPLICATION_JSON), deviceDiscoveryHandler::getAllRegisteredDevices)
        .GET("/devices/", accept(MediaType.APPLICATION_JSON), deviceDiscoveryHandler::getAllRegisteredDevices)
        .path("/topology", builder -> builder
            .GET("", accept(MediaType.APPLICATION_JSON), deviceDiscoveryHandler::getFullTopology)
            .GET("/", accept(MediaType.APPLICATION_JSON), deviceDiscoveryHandler::getFullTopology)
            .GET("/{macAddress}", accept(MediaType.APPLICATION_JSON), deviceDiscoveryHandler::getDeviceTopology))
        .build();
  }
}
