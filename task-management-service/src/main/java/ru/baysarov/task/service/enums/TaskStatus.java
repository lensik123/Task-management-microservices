package ru.baysarov.task.service.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import ru.baysarov.task.service.exception.InvalidEnumValueException;
import ru.baysarov.task.service.util.EnumUtil;


public enum TaskStatus {
  WAITING,
  IN_PROCESS,
  DONE;

  /**
   * Создает экземпляр {@link TaskStatus} из строки.
   *
   * @param value строковое представление статуса задачи
   * @return соответствующий {@link TaskStatus}
   * @throws InvalidEnumValueException если передано недопустимое значение
   */
  @JsonCreator
  public static TaskStatus fromString(String value) {
    try {
      return TaskStatus.valueOf(value.toUpperCase());
    } catch (IllegalArgumentException e) {
      String values = EnumUtil.getEnumValues(TaskStatus.class);
      throw new InvalidEnumValueException(
          "\nInvalid task status: " + value + ". \nMust be one of: " + values, value, TaskStatus.class);
    }
  }

  /**
   * Возвращает строковое представление статуса задачи для сериализации в JSON.
   *
   * @return строковое представление статуса задачи
   */
  @JsonValue
  public String toJson() {
    return name();
  }
}
