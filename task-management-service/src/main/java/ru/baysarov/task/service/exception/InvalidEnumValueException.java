package ru.baysarov.task.service.exception;

/**
 * Исключение, выбрасываемое, когда предоставлено недопустимое значение для типа перечисления.
 */
public class InvalidEnumValueException extends RuntimeException {
  private final String invalidValue;
  private final Class<?> enumClass;

  /**
   * Конструирует новое исключение InvalidEnumValueException с указанным сообщением,
   * недопустимым значением и классом перечисления.
   *
   * @param message сообщение, описывающее причину исключения
   * @param invalidValue недопустимое значение, вызвавшее исключение
   * @param enumClass класс перечисления, к которому принадлежит недопустимое значение
   */
  public InvalidEnumValueException(String message, String invalidValue, Class<?> enumClass) {
    super(message);
    this.invalidValue = invalidValue;
    this.enumClass = enumClass;
  }

  /**
   * Возвращает недопустимое значение, вызвавшее это исключение.
   *
   * @return недопустимое значение
   */
  public String getInvalidValue() {
    return invalidValue;
  }

  /**
   * Возвращает класс перечисления, к которому принадлежит недопустимое значение.
   *
   * @return класс перечисления
   */
  public Class<?> getEnumClass() {
    return enumClass;
  }
}
