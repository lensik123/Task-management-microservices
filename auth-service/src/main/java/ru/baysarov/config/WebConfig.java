package ru.baysarov.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class WebConfig implements WebMvcConfigurer {

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**") // Разрешить доступ ко всем путям
        .allowedOrigins("http://localhost:9092") // Разрешить запросы с api-gateway
        .allowedMethods("GET", "POST", "PUT", "DELETE") // Разрешенные методы
        .allowedHeaders("*") // Разрешить все заголовки
        .allowCredentials(true); // Разрешить куки
  }
}
