package ru.baysarov.statistic.service.impl;

import java.util.List;
import org.springframework.stereotype.Service;
import ru.baysarov.statistic.dto.UserDto;
import ru.baysarov.statistic.feign.UserClient;
import ru.baysarov.statistic.service.UserService;


@Service
public class UserServiceImpl implements UserService {

  private final UserClient userClient;

  public UserServiceImpl(UserClient userClient) {
    this.userClient = userClient;
  }

  @Override
  public List<String> getUserRoles(String email) {
    return userClient.getUserRoles(email);
  }

  @Override
  public UserDto getUserById(int id) {
    return userClient.getUserById(id).getBody();
  }

  @Override
  public UserDto getUserByEmail(String email) {
    return userClient.getUserByEmail(email).getBody();
  }
}
