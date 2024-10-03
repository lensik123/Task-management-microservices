package ru.baysarov.task.service.exception;

public class InvalidEnumValueException extends RuntimeException {
  private final String invalidValue;

  public InvalidEnumValueException(String message, String invalidValue) {
    super(message);
    this.invalidValue = invalidValue;
  }

  public String getInvalidValue() {
    return invalidValue;
  }
}
