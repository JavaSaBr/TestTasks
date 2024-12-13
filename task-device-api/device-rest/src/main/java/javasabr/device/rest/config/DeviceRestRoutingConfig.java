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
        .path("/device", builder -> builder
            .POST("/{deviceType}/{macAddress}", deviceRegisterHandler::register)
            .POST("/{deviceType}/{macAddress}/{uplinkMacAddress}", deviceRegisterHandler::registerConnected)
            .GET("/{macAddress}", accept(MediaType.APPLICATION_JSON), deviceDiscoveryHandler::getRegisteredDevice))
        .GET("/devices", deviceDiscoveryHandler::getAllRegisteredDevices)
        .GET("/topology", deviceDiscoveryHandler::getFullTopology)
        .GET("/topology/{macAddress}", deviceDiscoveryHandler::getDeviceTopology)
        .build();
  }
}
