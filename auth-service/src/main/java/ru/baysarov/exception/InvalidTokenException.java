package ru.baysarov.exception;

/**
 * Исключение, возникающее при попытке использования недействительного токена.
 */
public class InvalidTokenException extends RuntimeException {


  public InvalidTokenException(String message) {
    super(message);
  }
}
