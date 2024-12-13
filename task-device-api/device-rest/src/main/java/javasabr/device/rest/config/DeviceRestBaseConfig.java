package javasabr.device.rest.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.blackbird.BlackbirdModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import javasabr.device.rest.DeviceFieldsValidator;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class DeviceRestBaseConfig {

  @Bean
  DeviceFieldsValidator deviceFieldsValidator() {
    return new DeviceFieldsValidator();
  }

  @Bean
  @NotNull Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
    return builder -> {
      builder.modules(new BlackbirdModule(), new ParameterNamesModule(), new JavaTimeModule());
      builder.featuresToEnable(SerializationFeature.INDENT_OUTPUT);
      builder.serializationInclusion(JsonInclude.Include.NON_NULL);
    };
  }
}
