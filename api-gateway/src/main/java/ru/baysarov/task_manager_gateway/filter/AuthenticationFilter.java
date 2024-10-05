package ru.baysarov.task_manager_gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import ru.baysarov.task_manager_gateway.dto.UserDto;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

  private final WebClient.Builder webClientBuilder;
  private final RouteValidator routeValidator;

  public AuthenticationFilter(WebClient.Builder webClientBuilder, RouteValidator routeValidator) {
    super(Config.class);
    this.webClientBuilder = webClientBuilder;
    this.routeValidator = routeValidator;
  }

  @Override
  public GatewayFilter apply(Config config) {
    return (exchange, chain) -> {
      ServerWebExchange serverWebExchange = exchange;

      // Проверка на открытые маршруты, которые не требуют авторизации
      if (routeValidator.isSecured.test(serverWebExchange.getRequest())) {

        if (!serverWebExchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
          throw new RuntimeException("Missing authorization information");
        }

        String authHeader = serverWebExchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
        String[] parts = authHeader.split(" ");

        if (parts.length != 2 || !"Bearer".equals(parts[0])) {
          throw new RuntimeException("Incorrect authorization structure");
        }

        String token = parts[1];  // Сохраняем токен для передачи дальше

        // Валидация токена через Auth-сервис
        return webClientBuilder.build()
            .get()
            .uri("http://AUTH/auth/validateToken?token=" + token)
            .retrieve()
            .bodyToMono(UserDto.class)
            .map(userDto -> {
              ServerWebExchange mutatedExchange = exchange.mutate()
                  .request(serverWebExchange.getRequest().mutate()
                      .header("X-auth-user-email", userDto.getEmail())
                      .header(HttpHeaders.AUTHORIZATION, authHeader)
                      .build())
                  .build();

              return mutatedExchange;
            })
            .flatMap(chain::filter);
      }

      return chain.filter(exchange);
    };
  }

  public static class Config {
    // Пустой класс Config, можно добавить параметры фильтра если нужно
  }
}


