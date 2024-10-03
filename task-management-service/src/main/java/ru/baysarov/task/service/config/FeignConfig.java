package ru.baysarov.task.service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.baysarov.task.service.feign.FeignErrorDecoder;

@Configuration
public class FeignConfig {

  @Bean
  public FeignErrorDecoder errorDecoder() {
    return new FeignErrorDecoder();
  }
}