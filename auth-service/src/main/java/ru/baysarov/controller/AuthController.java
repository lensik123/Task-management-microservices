package ru.baysarov.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.baysarov.dto.AuthRequest;
import ru.baysarov.dto.UserDto;
import ru.baysarov.model.UserCredential;
import ru.baysarov.service.AuthService;
import ru.baysarov.service.UserService;



//TODO: добавить ошибки если не смог залогиниться и для других методов. Почему при изменении роли в бд, пользователь не может залогиниться?
//TODO: javadoc
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

  @PostMapping("/register")
  public ResponseEntity<?> addNewUser(@RequestBody UserCredential user) {
    log.info("Trying to add new user");
    userService.saveUser(user);
    log.info("User has been added");
    return ResponseEntity.ok(HttpStatus.CREATED);
  }

  @PostMapping("/token")
  public String getToken(@RequestBody AuthRequest authRequest) {
    return authService.authenticateAndReturnToken(authRequest);
  }

  /**
   *
   * @param token
   * @return ResponseEntity<UserDto> а
   */
  //TODO: должна возвращаться понятная ошибка в api-gateway
  @GetMapping("/validateToken")
  public ResponseEntity<UserDto> validateToken(@RequestParam String token) {
    log.info("Trying to validate token {}", token);
    return ResponseEntity.ok(authService.validateToken(token));

  }
}
