package javasabr.device.rest.config

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Import([
    DeviceRestBaseConfig,
    DeviceRestHandlersConfig,
    DeviceRestRoutingConfig
])
@Configuration(proxyBeanMethods = false)
class TestConfig {
}
