package ru.baysarov.statistic.exception;

import java.nio.file.AccessDeniedException;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import ru.baysarov.statistic.dto.ErrorResponse;


/**
 * Глобальный обработчик исключений для обработки различных типов исключений в приложении.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  /**
   * Обрабатывает исключения типа ResponseStatusException.
   *
   * @param ex исключение, которое необходимо обработать
   * @return ResponseEntity с кодом статуса и причиной исключения
   */
  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity<String> handleFeignException(ResponseStatusException ex) {
    logger.error(ex.getMessage(), ex);
    return ResponseEntity.status(ex.getStatusCode()).body(ex.getReason());
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



  /**
   * Обрабатывает исключения типа TaskNotFoundException.
   *
   * @param ex исключение, которое необходимо обработать
   * @return ResponseEntity с кодом статуса 404 и подробной информацией об ошибке
   */
  @ExceptionHandler(TaskNotFoundException.class)
  public ResponseEntity<?> handleTaskNotFoundException(TaskNotFoundException ex) {
    logger.error("TaskNotFoundException occurred: ", ex);
    ErrorResponse errorResponse = new ErrorResponse(
        "TaskNotFound",
        "The task with the specified ID was not found",
        Map.of("taskId", ex.getTaskId())
    );

    return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body(errorResponse);
  }
}
