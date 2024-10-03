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

        // Проверка наличия Authorization заголовка
        if (!serverWebExchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
          throw new RuntimeException("Missing authorization information");
        }

        String authHeader = serverWebExchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
        String[] parts = authHeader.split(" ");

        // Проверка структуры заголовка авторизации
        if (parts.length != 2 || !"Bearer".equals(parts[0])) {
          throw new RuntimeException("Incorrect authorization structure");
        }

        // Валидация токена через Auth-сервис
        return webClientBuilder.build()
            .get()
            .uri("http://AUTH/auth/validateToken?token=" + parts[1])
            .retrieve().bodyToMono(UserDto.class)
            .map(userDto -> {
              // Добавление ID пользователя в заголовок для последующих сервисов
              serverWebExchange.getRequest()
                  .mutate()
                  .header("X-auth-user-id", String.valueOf(userDto.getId()));
              return exchange;
            })
            .flatMap(chain::filter);
      }

      return chain.filter(exchange);
    };
  }

  public static class Config {
    // пустой класс, так как в данном случае не нужны дополнительные конфигурации
  }
}
