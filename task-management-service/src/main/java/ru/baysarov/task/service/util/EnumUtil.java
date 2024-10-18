package ru.baysarov.task.service.util;

import java.util.EnumSet;
import java.util.stream.Collectors;

/**
 * Утилитарный класс для работы с перечислениями (Enums).
 */
public class EnumUtil {

  /**
   * Получает строку, содержащую все значения перечисления в верхнем регистре, разделенные запятой.
   *
   * @param enumClass класс перечисления, значения которого нужно получить
   * @param <E>       тип перечисления
   * @return строка, содержащая все значения перечисления в верхнем регистре, разделенные запятой
   */
  public static <E extends Enum<E>> String getEnumValues(Class<E> enumClass) {
    return EnumSet.allOf(enumClass).stream()
        .map(Enum::name)
        .map(String::toUpperCase)
        .collect(Collectors.joining(", "));
  }
}
