package ru.baysarov.task.service.service;

import java.util.List;
import ru.baysarov.task.service.dto.UserDto;

public interface UserService {

   UserDto getUserById(int id);
   UserDto getUserByEmail(String email);
   List<String> getUserRoles(String email);

}
