package ru.baysarov.controller;


import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.baysarov.dto.UserDto;
import ru.baysarov.service.UserService;

@RestController
@RequestMapping("/user")
public class UserController {


  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }


  @GetMapping("/{email}")
  public ResponseEntity<?> getUserByEmail(@PathVariable String email) {
    try {
      UserDto user = userService.findByEmail(email);
      return new ResponseEntity<>(user, HttpStatus.OK);
    } catch (RuntimeException e) {
      return ResponseEntity.notFound().build();
    }
  }

  @GetMapping("/id/{id}")
  public ResponseEntity<?> getUserByEmail(@PathVariable int id) {
    try {
      UserDto user = userService.findById(id);
      return new ResponseEntity<>(user, HttpStatus.OK);
    } catch (RuntimeException e) {
      return ResponseEntity.notFound().build();
    }
  }


  @GetMapping("/{email}/roles")
  public List<String> getUserRoles(@PathVariable String email) {
    return userService.getUserRoles(email);
  }
}
