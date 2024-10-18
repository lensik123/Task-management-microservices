package ru.baysarov.task.service.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import ru.baysarov.task.service.exception.InvalidEnumValueException;
import ru.baysarov.task.service.util.EnumUtil;


public enum TaskPriority {
  HIGH,
  MEDIUM,
  LOW;

  /**
   * Создает экземпляр {@link TaskPriority} из строки.
   *
   * @param value строковое представление приоритета задачи
   * @return соответствующий {@link TaskPriority}
   * @throws InvalidEnumValueException если передано недопустимое значение
   */
  @JsonCreator
  public static TaskPriority fromString(String value) {
    try {
      return TaskPriority.valueOf(value.toUpperCase());
    } catch (IllegalArgumentException e) {
      String values = EnumUtil.getEnumValues(TaskPriority.class);
      throw new InvalidEnumValueException(
          "\nInvalid task priority: " + value + ". \nMust be one of: " + values, value,
          TaskPriority.class);
    }
  }

  /**
   * Возвращает строковое представление приоритета задачи для сериализации в JSON.
   *
   * @return строковое представление приоритета задачи
   */
  @JsonValue
  public String toJson() {
    return name();
  }
}
