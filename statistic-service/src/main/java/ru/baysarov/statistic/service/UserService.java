package ru.baysarov.statistic.service;

import java.util.List;
import ru.baysarov.statistic.dto.UserDto;

public interface UserService {

  UserDto getUserById(int id);
  UserDto getUserByEmail(String email);
  List<String> getUserRoles(String email);

}
