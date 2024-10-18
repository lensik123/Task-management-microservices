package ru.baysarov.task.service.exception;

import java.nio.file.AccessDeniedException;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import ru.baysarov.task.service.dto.ErrorResponse;
import ru.baysarov.task.service.enums.TaskPriority;
import ru.baysarov.task.service.enums.TaskStatus;

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
   * Обрабатывает исключения типа InvalidEnumValueException.
   *
   * @param ex исключение, которое необходимо обработать
   * @return ResponseEntity с кодом статуса 400 и подробной информацией об ошибке
   */
  @ExceptionHandler(InvalidEnumValueException.class)
  public ResponseEntity<?> handleInvalidEnumValueException(InvalidEnumValueException ex) {
    logger.error("InvalidEnumValueException occurred: ", ex);

    String[] allowedValues;
    if (ex.getEnumClass().equals(TaskPriority.class)) {
      allowedValues = new String[]{"HIGH", "MEDIUM", "LOW"};
    } else if (ex.getEnumClass().equals(TaskStatus.class)) {
      allowedValues = new String[]{"WAITING", "IN_PROCESS", "DONE"};
    } else {
      allowedValues = new String[]{};
    }

    Map<String, Object> errorDetails = Map.of(
        "invalidValue", ex.getInvalidValue(),
        "allowedValues", allowedValues
    );

    ErrorResponse errorResponse = new ErrorResponse("InvalidEnumValue",
        "Invalid value for " + ex.getEnumClass().getSimpleName().toLowerCase(), errorDetails);
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(errorResponse);
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

  @ExceptionHandler(TaskAccessException.class)
  public ResponseEntity<ErrorResponse> handleTaskAccessException(TaskAccessException ex) {
    logger.error("TaskAccessException occurred: ", ex);
    ErrorResponse errorResponse = new ErrorResponse(
        "Task access",
        ex.getMessage() // Здесь вы можете указать сообщение об ошибке
    );
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse); // Статус 403 Forbidden
  }


}
