package ru.baysarov.task.service.feign;

import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.baysarov.task.service.dto.UserDto;

@FeignClient(name = "AUTH")
public interface UserClient {
  @GetMapping("/user/{id}")
  ResponseEntity<UserDto> getUserById(@PathVariable int id);

  @GetMapping("/user/{id}/roles")
  List<String> getUserRoles(@PathVariable("id") int userId);
}

