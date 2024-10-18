package ru.baysarov.statistic.feign;

import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.baysarov.statistic.dto.UserDto;

/**
 * Клиент для взаимодействия с сервисом аутентификации (AUTH) с использованием Feign.
 */
@FeignClient(name = "AUTH")
public interface UserClient {

  /**
   * Получает информацию о пользователе по его адресу электронной почты.
   *
   * @param email адрес электронной почты пользователя
   * @return ResponseEntity с данными пользователя в формате UserDto
   */
  @GetMapping("/user/{email}")
  ResponseEntity<UserDto> getUserByEmail(@PathVariable String email);

  @GetMapping("/user/id/{id}")
  ResponseEntity<UserDto> getUserById(@PathVariable int id);

  /**
   * Получает роли пользователя по его адресу электронной почты.
   *
   * @param email адрес электронной почты пользователя
   * @return список ролей пользователя
   */
  @GetMapping("/user/{email}/roles")
  List<String> getUserRoles(@PathVariable("email") String email);
}
