package ru.baysarov.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO класс для представления пользователя.
 * Содержит идентификатор пользователя и его email.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

  private int id;
  private String email;

}
