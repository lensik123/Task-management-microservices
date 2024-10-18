package ru.baysarov.task.service.config;

import io.swagger.v3.oas.annotations.Hidden;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.springdoc.core.properties.AbstractSwaggerUiConfigProperties.SwaggerUrl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SwaggerUiConfig {

  @Autowired
  private DiscoveryClient discoveryClient;

  @Hidden
  @GetMapping("/swagger-config.json")
  public Map<String, Object> swaggerConfig() {
    List<SwaggerUrl> urls = new LinkedList<>();
    discoveryClient.getServices().forEach(serviceName ->
        discoveryClient.getInstances(serviceName).forEach(serviceInstance ->
            urls.add(
                new SwaggerUrl(serviceName, serviceInstance.getUri() + "/v3/api-docs", serviceName))
        )
    );
    return Map.of("urls", urls);
  }
}