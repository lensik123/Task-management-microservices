package ru.baysarov.task.service.util;


import java.util.EnumSet;
import java.util.stream.Collectors;

public class EnumUtil {

  public static <E extends Enum<E>> String getEnumValues(Class<E> enumClass) {
    return EnumSet.allOf(enumClass).stream()
        .map(Enum::name)
        .map(String::toUpperCase)
        .collect(Collectors.joining(", "));
  }
}
