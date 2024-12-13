package javasabr.device.rest.config;

import javasabr.device.service.DeviceService;
import javasabr.device.service.config.DeviceServiceConfig;
import javasabr.device.rest.DeviceFieldsValidator;
import javasabr.device.rest.handler.DeviceDiscoveryHandler;
import javasabr.device.rest.handler.DeviceRegisterHandler;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Import({
    DeviceServiceConfig.class,
    DeviceRestBaseConfig.class
})
@Configuration(proxyBeanMethods = false)
public class DeviceRestHandlersConfig {


  @Bean
  public DeviceDiscoveryHandler deviceDiscoveryHandler(
      @NotNull DeviceService deviceService,
      @NotNull DeviceFieldsValidator deviceFieldsValidator) {
    return new DeviceDiscoveryHandler(deviceService, deviceFieldsValidator);
  }

  @Bean
  public DeviceRegisterHandler deviceRegisterHandler(
      @NotNull DeviceService deviceService,
      @NotNull DeviceFieldsValidator deviceFieldsValidator) {
    return new DeviceRegisterHandler(deviceService, deviceFieldsValidator);
  }
}
