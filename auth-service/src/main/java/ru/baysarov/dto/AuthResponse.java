package ru.baysarov.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * DTO класс для ответа аутентификации.
 * Содержит токен, возвращаемый после успешной аутентификации пользователя.
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
  private String token;
}
