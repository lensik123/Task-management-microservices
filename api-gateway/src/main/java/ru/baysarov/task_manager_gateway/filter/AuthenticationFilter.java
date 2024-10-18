package ru.baysarov.task_manager_gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import ru.baysarov.task_manager_gateway.dto.UserDto;

/**
 * Фильтр аутентификации для проверки токена в заголовке Authorization.
 * <p>
 * Этот фильтр проверяет, присутствует ли токен авторизации в запросе, валидирует его через
 * Auth-сервис и добавляет информацию о пользователе в запрос.
 */
@Component
@Slf4j
public class AuthenticationFilter extends
    AbstractGatewayFilterFactory<AuthenticationFilter.Config> {


  private final WebClient.Builder webClientBuilder;
  private final RouteValidator routeValidator;

  /**
   * Конструктор для AuthenticationFilter.
   *
   * @param webClientBuilder сборщик WebClient для выполнения запросов к Auth-сервису
   * @param routeValidator   валидатор маршрутов для определения защищенных маршрутов
   */
  public AuthenticationFilter(WebClient.Builder webClientBuilder, RouteValidator routeValidator) {
    super(Config.class);
    this.webClientBuilder = webClientBuilder;
    this.routeValidator = routeValidator;
  }

  /**
   * Применяет фильтр для обработки запросов.
   *
   * @param config конфигурация фильтра
   * @return GatewayFilter для выполнения в цепочке фильтров
   */
  @Override
  public GatewayFilter apply(Config config) {
    return (exchange, chain) -> {
      ServerWebExchange serverWebExchange = exchange;

      if (routeValidator.isSecured.test(serverWebExchange.getRequest())) {
        if (!serverWebExchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
          log.warn("Authorization header is missing");
          return handleUnauthorizedResponse(exchange, "Missing authorization information");
        }

        String authHeader = serverWebExchange.getRequest().getHeaders()
            .get(HttpHeaders.AUTHORIZATION).get(0);
        String[] parts = authHeader.split(" ");

        if (parts.length != 2 || !"Bearer".equals(parts[0])) {
          log.warn("Incorrect authorization structure: {}", authHeader);
          return handleUnauthorizedResponse(exchange, "Incorrect authorization structure");
        }

        String token = parts[1];

        log.info("Validating token: {}", token);
        return webClientBuilder.build()
            .get()
            .uri("http://AUTH/auth/validateToken?token=" + token)
            .retrieve()
            .bodyToMono(UserDto.class)
            .flatMap(userDto -> {
              log.info("Token validated successfully for user: {}", userDto.getEmail());
              ServerWebExchange mutatedExchange = exchange.mutate()
                  .request(serverWebExchange.getRequest().mutate()
                      .header("X-auth-user-email", userDto.getEmail())
                      .header(HttpHeaders.AUTHORIZATION, authHeader)
                      .build())
                  .build();

              return chain.filter(mutatedExchange);
            })
            .onErrorResume(throwable -> {
              log.error("Error", throwable.getMessage());
              return handleUnauthorizedResponse(exchange, "Authenticate error (invalid token or service unavailable)");
            });
      }

      return chain.filter(exchange);
    };
  }

  /**
   * Обрабатывает ответ с кодом 401 Unauthorized.
   *
   * @param exchange     объект обмена для текущего запроса
   * @param errorMessage сообщение об ошибке
   * @return Mono<Void> для завершения обработки ответа
   */
  private Mono<Void> handleUnauthorizedResponse(ServerWebExchange exchange, String errorMessage) {
    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
    exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
    String errorResponse = "{\"error\": \"" + errorMessage + "\"}";
    DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(errorResponse.getBytes());
    return exchange.getResponse().writeWith(Mono.just(buffer));
  }

  /**
   * Конфигурация для AuthenticationFilter. Пустой класс, можно добавить параметры фильтра, если
   * нужно.
   */
  public static class Config {

  }
}
