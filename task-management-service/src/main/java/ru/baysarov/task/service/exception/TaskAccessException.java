package ru.baysarov.task.service.exception;

/**
 * Исключение, выбрасываемое, когда доступ к задаче запрещен.
 */
public class TaskAccessException extends RuntimeException {

  /**
   * Конструирует новое исключение TaskAccessException с указанным сообщением.
   *
   * @param message сообщение, описывающее причину исключения
   */
  public TaskAccessException(String message) {
    super(message);
  }
}
