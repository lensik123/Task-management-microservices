package ru.baysarov.statistic.feign;

import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.expression.AccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import ru.baysarov.statistic.exception.UserNotFoundException;

/**
 * Декодер ошибок для Feign, который обрабатывает различные коды состояния HTTP и генерирует
 * соответствующие исключения.
 */
public class FeignErrorDecoder implements ErrorDecoder {

  /**
   * Декодирует ответ от сервиса и возвращает соответствующее исключение на основе кода состояния
   * HTTP.
   *
   * @param methodKey ключ метода Feign, который был вызван
   * @param response  ответ от сервиса
   * @return исключение, соответствующее коду состояния ответа
   */
  @Override
  public Exception decode(String methodKey, Response response) {
    HttpStatus status = HttpStatus.valueOf(response.status());

    String url = response.request().url();
    String userIdentifier = extractUserIdentifierFromUrl(url);

    if (status == HttpStatus.NOT_FOUND) {
      return new UserNotFoundException(
          "User with identifier " + userIdentifier + " not found in system");
    } else if (status == HttpStatus.FORBIDDEN) {
      return new AccessException("Access denied for user with identifier " + userIdentifier);
    }

    return new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
        "Internal server error in auth service");
  }

  /**
   * Извлекает идентификатор пользователя (email или ID) из URL.
   *
   * @param url URL запроса, содержащий идентификатор пользователя
   * @return строка, представляющая идентификатор пользователя
   */
  private String extractUserIdentifierFromUrl(String url) {
    return url.substring(url.lastIndexOf('/') + 1);
  }
}
