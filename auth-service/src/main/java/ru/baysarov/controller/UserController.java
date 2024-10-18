package ru.baysarov.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import ru.baysarov.dto.UserDto;
import ru.baysarov.service.UserService;

@RestController
@RequestMapping("/user")
public class UserController {

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  /**
   * Получение пользователя по электронной почте.
   *
   * @param email адрес электронной почты пользователя.
   * @return Пользователь по указанному адресу электронной почты или 404, если не найден.
   */
  @Operation(summary = "Получение пользователя по электронной почте",
      responses = {
          @ApiResponse(responseCode = "200", description = "Пользователь найден"),
          @ApiResponse(responseCode = "404", description = "Пользователь не найден")
      })
  @GetMapping("/{email}")
  public ResponseEntity<?> getUserByEmail(@PathVariable @Parameter(description = "Email пользователя") String email) {
    try {
      UserDto user = userService.findByEmail(email);
      return new ResponseEntity<>(user, HttpStatus.OK);
    } catch (RuntimeException e) {
      return ResponseEntity.notFound().build();
    }
  }

  /**
   * Получение пользователя по идентификатору.
   *
   * @param id идентификатор пользователя.
   * @return Пользователь с указанным идентификатором или 404, если не найден.
   */
  @Operation(summary = "Получение пользователя по идентификатору",
      responses = {
          @ApiResponse(responseCode = "200", description = "Пользователь найден"),
          @ApiResponse(responseCode = "404", description = "Пользователь не найден")
      })
  @GetMapping("/id/{id}")
  public ResponseEntity<?> getUserById(@PathVariable @Parameter(description = "Идентификатор пользователя") int id) {
    try {
      UserDto user = userService.findById(id);
      return new ResponseEntity<>(user, HttpStatus.OK);
    } catch (RuntimeException e) {
      return ResponseEntity.notFound().build();
    }
  }

  /**
   * Получение ролей пользователя по электронной почте.
   *
   * @param email адрес электронной почты пользователя.
   * @return Список ролей пользователя.
   */
  @Operation(summary = "Получение ролей пользователя",
      responses = {
          @ApiResponse(responseCode = "200", description = "Список ролей пользователя"),
          @ApiResponse(responseCode = "404", description = "Пользователь не найден")
      })
  @GetMapping("/{email}/roles")
  public List<String> getUserRoles(@PathVariable @Parameter(description = "Email пользователя") String email) {
    return userService.getUserRoles(email);
  }
}
