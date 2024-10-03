package ru.baysarov.controller;


import java.util.List;
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

  @GetMapping("/{id}")
  public UserDto getUser(@PathVariable int id) {
    return userService.findById(id);
  }

  @GetMapping("/{id}/roles")
  public List<String> getUserRoles(@PathVariable int id) {
    return userService.getUserRoles(id);
  }
}
