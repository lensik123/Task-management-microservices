package ru.baysarov.task_manager_gateway.filter;

import java.util.List;
import java.util.function.Predicate;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

@Component
public class RouteValidator {

  public static final List<String> openApiEndpoints = List.of(
      "/auth/register",
      "/auth/token",
      "/eureka"
  );

  public Predicate<ServerHttpRequest> isSecured =
      serverHttpRequest -> openApiEndpoints
          .stream()
          .noneMatch(uri -> serverHttpRequest.getURI().getPath().contains(uri));
}
