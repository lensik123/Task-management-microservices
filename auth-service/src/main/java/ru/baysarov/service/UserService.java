package ru.baysarov.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.baysarov.dto.UserDto;
import ru.baysarov.enums.Role;
import ru.baysarov.model.UserCredential;
import ru.baysarov.repository.UserRepository;

//TODO: сделать интерфейс для сервиса. Сделать Exception
@Service
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

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    UserCredential user = userRepository.findByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException("User " + email + " not found"));

    return new org.springframework.security.core.userdetails.User(user.getEmail(),
        user.getPassword(), new ArrayList<>());
  }

  public void saveUser(UserCredential credential) {
    credential.setPassword(passwordEncoder.encode(credential.getPassword()));
    credential.setRole(Role.USER);
    repository.save(credential);
  }


  public UserDto findById(int id) {
    UserCredential user = repository.findById(id)
        .orElseThrow(() -> new RuntimeException("User not found"));
    UserDto userDto = new UserDto();
    userDto.setEmail(user.getEmail());
    userDto.setId(user.getId());
    return userDto;
  }


  public List<String> getUserRoles(int id) {
    UserCredential user = userRepository.findById(id).orElseThrow(()-> new RuntimeException("User not found"));
    List<String> roles = new ArrayList<>();
    roles.add(user.getRole().toString());
    return roles;
  }
}
