package ru.baysarov.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.baysarov.dto.RegisterRequest;
import ru.baysarov.dto.UserDto;
import ru.baysarov.enums.Role;
import ru.baysarov.exception.UserAlreadyExistsException;
import ru.baysarov.exception.UserNotFoundException;
import ru.baysarov.model.UserCredential;
import ru.baysarov.repository.UserRepository;

/**
 * Сервис для управления пользователями, реализующий функционал {@link UserDetailsService}.
 * Предоставляет методы для загрузки, сохранения и получения информации о пользователях.
 */
@Slf4j
@Service
@Transactional(readOnly = true)
public class UserService implements UserDetailsService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final UserRepository repository;

  public UserService(UserRepository userRepository,
      PasswordEncoder passwordEncoder, UserRepository repository) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.repository = repository;
  }

  /**
   * Загружает пользователя по его адресу электронной почты.
   *
   * @param email адрес электронной почты пользователя
   * @return объект {@link UserDetails} с информацией о пользователе
   * @throws UsernameNotFoundException если пользователь не найден
   */
  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    log.info("Loading user by email: {}", email);
    UserCredential user = userRepository.findByEmail(email)
        .orElseThrow(() -> {
          log.error("User {} not found", email);
          return new UsernameNotFoundException("User " + email + " not found");
        });

    return new org.springframework.security.core.userdetails.User(user.getEmail(),
        user.getPassword(), new ArrayList<>());
  }

  /**
   * Сохраняет нового пользователя в базе данных.
   *
   * @param registerRequest объект {@link RegisterRequest} с данными пользователя
   */
  @Transactional
  public void saveUser(RegisterRequest registerRequest) {

    Optional<UserCredential> existingUser = repository.findByEmail(registerRequest.getEmail());
    if (existingUser.isPresent()) {
      log.error("Email already in use: {}", registerRequest.getEmail());
      throw new UserAlreadyExistsException("Email: " +registerRequest.getEmail() + " is already in use"); // Создайте этот класс исключения
    }
    log.info("Saving user: {}", registerRequest.getEmail());
    UserCredential userCredential = new UserCredential();
    userCredential.setEmail(registerRequest.getEmail());
    userCredential.setFirstName(registerRequest.getFirstName());
    userCredential.setLastName(registerRequest.getLastName());
    userCredential.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
    userCredential.setRole(Role.USER);
    repository.save(userCredential);
    log.info("User saved successfully: {}", registerRequest.getEmail());
  }

  /**
   * Находит пользователя по адресу электронной почты.
   *
   * @param email адрес электронной почты пользователя
   * @return объект {@link UserDto} с информацией о пользователе
   * @throws UserNotFoundException если пользователь не найден
   */
  public UserDto findByEmail(String email) {
    log.info("Finding user by email: {}", email);
    UserCredential user = repository.findByEmail(email)
        .orElseThrow(() -> {
          log.error("User not found for email: {}", email);
          return new UserNotFoundException("User not found");
        });

    return convertToDto(user);
  }

  /**
   * Находит пользователя по его идентификатору.
   *
   * @param id идентификатор пользователя
   * @return объект {@link UserDto} с информацией о пользователе
   * @throws UserNotFoundException если пользователь не найден
   */
  public UserDto findById(int id) {
    log.info("Finding user by id: {}", id);
    UserCredential user = repository.findById(id)
        .orElseThrow(() -> {
          log.error("User not found for id: {}", id);
          return new UserNotFoundException("User not found");
        });

    return convertToDto(user);
  }

  /**
   * Получает роли пользователя по его адресу электронной почты.
   *
   * @param email адрес электронной почты пользователя
   * @return список строк с ролями пользователя
   * @throws UserNotFoundException если пользователь не найден
   */
  public List<String> getUserRoles(String email) {
    log.info("Getting roles for user: {}", email);
    UserCredential user = repository.findByEmail(email)
        .orElseThrow(() -> new UserNotFoundException("User not found: " + email));

    List<String> roles = new ArrayList<>();
    roles.add(user.getRole().toString());
    log.info("Roles found for user {}: {}", email, roles);

    return roles;
  }

  /**
   * Преобразует объект {@link UserCredential} в {@link UserDto}.
   *
   * @param user объект {@link UserCredential} для преобразования
   * @return объект {@link UserDto} с информацией о пользователе
   */
  public UserDto convertToDto(UserCredential user) {
    UserDto userDto = new UserDto();
    userDto.setEmail(user.getEmail());
    userDto.setId(user.getId());
    log.info("User found: {}", userDto);
    return userDto;
  }
}
