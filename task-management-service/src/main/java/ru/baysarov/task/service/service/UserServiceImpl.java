package ru.baysarov.task.service.service;

import java.util.List;
import org.springframework.stereotype.Service;
import ru.baysarov.task.service.dto.UserDto;
import ru.baysarov.task.service.feign.UserClient;

/**
 * Реализация сервиса для работы с пользователями.
 */
@Service
public class UserServiceImpl implements UserService {

  private final UserClient userClient;


  public UserServiceImpl(UserClient userClient) {
    this.userClient = userClient;
  }

  /**
   * Получает роли пользователя по его email.
   *
   * @param email email пользователя.
   * @return список ролей пользователя.
   */
  @Override
  public List<String> getUserRoles(String email) {
    return userClient.getUserRoles(email);
  }

  /**
   * Получает пользователя по его идентификатору.
   *
   * @param id идентификатор пользователя.
   * @return объект UserDto с информацией о пользователе.
   */
  @Override
  public UserDto getUserById(int id) {
    return userClient.getUserById(id).getBody();
  }

  /**
   * Получает пользователя по его email.
   *
   * @param email email пользователя.
   * @return объект UserDto с информацией о пользователе.
   */
  @Override
  public UserDto getUserByEmail(String email) {
    return userClient.getUserByEmail(email).getBody();
  }
}
