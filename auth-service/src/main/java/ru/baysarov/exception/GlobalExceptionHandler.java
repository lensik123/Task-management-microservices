package ru.baysarov.exception;

import java.nio.file.AccessDeniedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


/**
 * Глобальный обработчик исключений для обработки различных типов исключений в приложении.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  /**
   * Обрабатывает исключения типа UserNotFoundException.
   *
   * @param ex исключение, которое необходимо обработать
   * @return ResponseEntity с кодом статуса 404 и сообщением об ошибке
   */
  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<String> handleUserNotFoundException(UserNotFoundException ex) {
    logger.error(ex.getMessage(), ex);
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
  }


  @ExceptionHandler(UserAlreadyExistsException.class)
  public ResponseEntity<String> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
    logger.error(ex.getMessage(), ex);
    return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
  }
  /**
   * Обрабатывает исключения типа AccessDeniedException.
   *
   * @param ex исключение, которое необходимо обработать
   * @return ResponseEntity с кодом статуса 403 и сообщением об ошибке
   */
  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<?> handleAccessDeniedException(AccessDeniedException ex) {
    logger.error("Access denied", ex);
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied: " + ex.getMessage());
  }
}
