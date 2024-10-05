package ru.baysarov.task.service.feign;

import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * Декодер ошибок для Feign, который обрабатывает различные коды состояния HTTP
 * и генерирует соответствующие исключения.
 */
public class FeignErrorDecoder implements ErrorDecoder {

  /**
   * Декодирует ответ от сервиса и возвращает соответствующее исключение на основе
   * кода состояния HTTP.
   *
   * @param methodKey ключ метода Feign, который был вызван
   * @param response ответ от сервиса
   * @return исключение, соответствующее коду состояния ответа
   */
  @Override
  public Exception decode(String methodKey, Response response) {
    HttpStatus status = HttpStatus.valueOf(response.status());

    if (status == HttpStatus.NOT_FOUND) {
      return new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found in auth service");
    } else if (status == HttpStatus.FORBIDDEN) {
      return new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied in auth service");
    }

    return new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error in auth service");
  }
}
