package ru.baysarov.task.service.exception;

/**
 * Исключение, выбрасываемое, когда пользователь не найден.
 */
public class UserNotFoundException extends RuntimeException {

  /**
   * Конструирует новое исключение UserNotFoundException с указанным сообщением.
   *
   * @param message сообщение, описывающее причину исключения
   */
  public UserNotFoundException(String message) {
    super(message);
  }
}
