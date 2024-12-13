package javasabr.device.service.config;

import javasabr.device.service.DeviceService;
import javasabr.device.service.impl.InMemoryDeviceService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class DeviceServiceConfig {

  @Bean
  public DeviceService inMemoryDeviceService() {
    return new InMemoryDeviceService();
  }
}
