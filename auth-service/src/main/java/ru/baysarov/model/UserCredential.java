package ru.baysarov.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Collection;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.baysarov.enums.Role;

/**
 * Модель для хранения данных о пользователе, реализующая интерфейс {@link UserDetails}.
 */
@Data
@Entity
@Table(name = "users")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCredential implements UserDetails {

  /**Id пользователя */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  /** Имя пользователя. */
  private String firstName;

  /** Фамилия пользователя. */
  private String lastName;

  /** Адрес электронной почты пользователя, используемый в качестве имени пользователя. */
  private String email;

  /** Пароль пользователя. */
  private String password;

  /** Роль пользователя в системе. */
  @Enumerated(EnumType.STRING)
  private Role role;

  /**
   * Возвращает роли пользователя в виде коллекции {@link GrantedAuthority}.
   *
   * @return коллекция ролей пользователя
   */
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority(role.name()));
  }

  /**
   * Возвращает имя пользователя.
   *
   * @return адрес электронной почты пользователя
   */
  @Override
  public String getUsername() {
    return email;
  }

  /**
   * Проверяет, не истек ли срок действия учетной записи.
   *
   * @return true, если учетная запись не истекла
   */
  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  /**
   * Проверяет, заблокирована ли учетная запись.
   *
   * @return true, если учетная запись не заблокирована
   */
  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  /**
   * Проверяет, не истек ли срок действия учетных данных.
   *
   * @return true, если учетные данные не истекли
   */
  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  /**
   * Проверяет, активна ли учетная запись.
   *
   * @return true, если учетная запись активна
   */
  @Override
  public boolean isEnabled() {
    return true;
  }
}
