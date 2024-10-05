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


@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);


  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity<String> handleFeignException(ResponseStatusException ex) {
    return ResponseEntity.status(ex.getStatusCode()).body(ex.getReason());
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<?> handleAccessDeniedException(AccessDeniedException ex) {
    logger.error("Access denied", ex);
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied: " + ex.getMessage());
  }

  @ExceptionHandler(InvalidEnumValueException.class)
  public ResponseEntity<?> handleInvalidEnumValueException(InvalidEnumValueException ex) {
    logger.error("InvalidEnumValueException occurred: ", ex);
    Map<String, Object> errorDetails = Map.of(
        "field", "taskPriority",
        "invalidValue", ex.getInvalidValue(),
        "allowedValues", new String[]{"HIGH", "MEDIUM", "LOW"}
    );

    ErrorResponse errorResponse = new ErrorResponse("InvalidEnumValue",
        "Invalid value for task priority", errorDetails);
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(errorResponse);
  }

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

