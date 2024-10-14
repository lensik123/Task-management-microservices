package ru.baysarov.exception;

/**
 * Исключение, выбрасываемое, когда пользователь не найден в системе.
 */
public class UserNotFoundException extends RuntimeException {


  public UserNotFoundException(String message) {
    super(message);
  }
}
