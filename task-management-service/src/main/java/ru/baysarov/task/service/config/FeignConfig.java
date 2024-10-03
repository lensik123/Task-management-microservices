package ru.baysarov.task.service.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.server.ServerWebExchange;

@Configuration
public class FeignConfig {

  @Bean
  public RequestInterceptor requestInterceptor() {
    return requestTemplate -> {
      // Получение токена из хранилища (например, из контекста)
      String token = "Bearer " + getTokenFromContext();
      requestTemplate.header("Authorization", token);
    };
  }

  private String getTokenFromContext() {
    return "ваш токен";
  }
}
