package ru.baysarov.task.service.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import ru.baysarov.task.service.exception.InvalidEnumValueException;
import ru.baysarov.task.service.util.EnumUtil;

public enum TaskPriority {
  HIGH,
  MEDIUM,
  LOW;

  @JsonCreator
  public static TaskPriority fromString(String value) {
    try {
      return TaskPriority.valueOf(value.toUpperCase());
    } catch (IllegalArgumentException e) {
      String values = EnumUtil.getEnumValues(TaskPriority.class);
      throw new InvalidEnumValueException("\nInvalid task priority: " + value + ". \nMust be one of: " + values, value);
    }
  }

  @JsonValue
  public String toJson() {
    return name();
  }
}
