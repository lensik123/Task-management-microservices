package ru.baysarov.task.service.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import ru.baysarov.task.service.exception.InvalidEnumValueException;
import ru.baysarov.task.service.util.EnumUtil;


public enum TaskStatus {
  WAITING,
  IN_PROCESS,
  DONE;

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

  @JsonValue
  public String toJson() {
    return name();
  }

}