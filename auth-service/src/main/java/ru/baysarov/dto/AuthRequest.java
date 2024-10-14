package ru.baysarov.dto;


import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


/**
 * DTO для передачи данных аутентификации пользователя.
 * Содержит email и пароль, необходимые для входа в систему.
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class AuthRequest {

  @NotEmpty(message = "Email is required")
  private String email;

  @NotEmpty(message = "Password cannot be empty")
  private String password;
}
