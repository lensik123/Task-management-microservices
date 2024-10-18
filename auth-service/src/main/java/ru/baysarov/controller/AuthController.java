package ru.baysarov.controller;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import ru.baysarov.dto.AuthRequest;
import ru.baysarov.dto.AuthResponse;
import ru.baysarov.dto.RegisterRequest;
import ru.baysarov.service.AuthService;
import ru.baysarov.service.UserService;

/**
 * Контроллер для обработки аутентификации и регистрации пользователей.
 */
@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthController {

  private final AuthService authService;
  private final UserService userService;

  @Autowired
  public AuthController(AuthService service, UserService userService) {
    this.authService = service;
    this.userService = userService;
  }

  /**
   * Регистрация нового пользователя.
   *
   * @param registerRequest Запрос на регистрацию, содержащий данные пользователя.
   * @param bindingResult Результаты валидации, которые могут содержать ошибки.
   * @return Ответ с кодом статуса 201 (Created) при успешной регистрации или ошибки валидации.
   */
  @Operation(summary = "Регистрация нового пользователя",
      responses = {
          @ApiResponse(responseCode = "201", description = "Пользователь успешно зарегистрирован"),
          @ApiResponse(responseCode = "400", description = "Ошибки валидации")
      })
  @PostMapping("/register")
  public ResponseEntity<?> registerUser(@RequestBody @Valid RegisterRequest registerRequest,
      BindingResult bindingResult) {
    ResponseEntity<?> errors = getResponseEntity(bindingResult);
    log.info("Trying to add new user");
    if (errors != null) {
      log.error("Validation errors during registration: {}", errors.getBody());
      return errors;
    }
    userService.saveUser(registerRequest);
    log.info("User has been added");
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  /**
   * Получение токена аутентификации для пользователя.
   *
   * @param authRequest Запрос на аутентификацию, содержащий данные пользователя.
   * @param bindingResult Результаты валидации, которые могут содержать ошибки.
   * @return Ответ с токеном при успешной аутентификации или ошибки валидации.
   */
  @Operation(summary = "Получение токена аутентификации",
      responses = {
          @ApiResponse(responseCode = "200", description = "Токен успешно получен"),
          @ApiResponse(responseCode = "400", description = "Ошибки валидации")
      })
  @PostMapping("/token")
  public ResponseEntity<?> getToken(@RequestBody @Valid AuthRequest authRequest,
      BindingResult bindingResult) {
    ResponseEntity<?> errors = getResponseEntity(bindingResult);
    log.info("Trying to authenticate user");
    if (errors != null) {
      log.error("Validation errors during authentication: {}", errors.getBody());
      return errors;
    }
    String token = authService.authenticateAndReturnToken(authRequest);
    return ResponseEntity.ok(new AuthResponse(token));
  }

  /**
   * Валидация токена аутентификации.
   *
   * @param token Токен для валидации.
   * @return Ответ с результатом валидации.
   */
  @Operation(summary = "Валидация токена",
      responses = {
          @ApiResponse(responseCode = "200", description = "Токен успешно валиден"),
          @ApiResponse(responseCode = "400", description = "Токен не валиден")
      })
  @GetMapping("/validateToken")
  public ResponseEntity<?> validateToken(@RequestParam @Parameter(description = "Токен для валидации") String token) {
    log.info("Trying to validate token {}", token);
    return ResponseEntity.ok(authService.validateToken(token));
  }

  /**
   * Обработка ошибок валидации.
   *
   * @param bindingResult Результаты валидации.
   * @return Ответ с ошибками валидации или null, если ошибок нет.
   */
  static ResponseEntity<?> getResponseEntity(BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      Map<String, String> errors = new HashMap<>();
      for (FieldError error : bindingResult.getFieldErrors()) {
        errors.put(error.getField(), error.getDefaultMessage());
      }
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }
    return null;
  }
}
