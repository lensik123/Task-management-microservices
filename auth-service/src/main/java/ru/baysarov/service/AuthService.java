package ru.baysarov.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.baysarov.dto.AuthRequest;
import ru.baysarov.dto.UserDto;
import ru.baysarov.exception.InvalidTokenException;
import ru.baysarov.exception.UserNotFoundException;
import ru.baysarov.repository.UserRepository;

/**
 * Сервис для аутентификации и управления токенами.
 * <p>
 * Этот сервис отвечает за аутентификацию пользователей, генерацию JWT токенов
 * и валидацию токенов для доступа к защищенным ресурсам.
 * </p>
 */
@Service
@Slf4j
public class AuthService {

  private final AuthenticationManager authenticationManager;
  private final UserRepository userRepository;

  @Value("${jwt.secret}")
  private String SECRET_KEY;

  @Value("${jwt.expiration-time}")
  private long expirationTimeInSeconds;

  /**
   * Конструктор для инициализации {@link AuthService}.
   *
   * @param authenticationManager менеджер аутентификации
   * @param userRepository         репозиторий для работы с пользователями
   */
  @Autowired
  public AuthService(AuthenticationManager authenticationManager, UserRepository userRepository) {
    this.authenticationManager = authenticationManager;
    this.userRepository = userRepository;
  }

  /**
   * Генерирует JWT токен для заданного email.
   *
   * @param email email пользователя, для которого генерируется токен
   * @return сгенерированный токен
   */
  public String generateToken(String email) {
    long expirationTimeInMillis = expirationTimeInSeconds * 1000;
    String token = Jwts.builder()
        .setSubject(email)
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + expirationTimeInMillis))
        .signWith(getSigninKey(), SignatureAlgorithm.HS256)
        .compact();

    log.info("Generated token for user: {}", email);
    return token;
  }

  /**
   * Проверяет валидность заданного токена и возвращает информацию о пользователе.
   *
   * @param token токен для проверки
   * @return {@link UserDto} с информацией о пользователе, если токен валиден
   * @throws InvalidTokenException если токен недействителен
   * @throws UserNotFoundException если пользователь не найден
   */
  @Transactional(readOnly = true)
  public UserDto validateToken(String token) {
    log.info("Trying to validate token: {}", token);

    String email;
    try {
      email = Jwts.parserBuilder()
          .setSigningKey(getSigninKey())
          .build()
          .parseClaimsJws(token)
          .getBody()
          .getSubject();
    } catch (Exception e) {
      log.error("Token validation failed: {}", e.getMessage());
      throw new InvalidTokenException("Invalid token: " + e.getMessage());
    }

    return userRepository.findByEmail(email)
        .map(user -> {
          log.info("Token is valid for user: {}", email);
          return new UserDto(user.getId(), user.getEmail());
        })
        .orElseThrow(() -> {
          log.error("User not found for email: {}", email);
          return new UserNotFoundException("User not found" + email);
        });
  }

  /**
   * Аутентифицирует пользователя и возвращает сгенерированный токен.
   *
   * @param request запрос аутентификации с email и паролем
   * @return сгенерированный JWT токен
   */
  public String authenticateAndReturnToken(AuthRequest request) {
    log.info("Authenticating user with email: {}", request.getEmail());

    var authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
    );

    var userDetails = (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
    String token = generateToken(userDetails.getUsername());

    log.info("User authenticated successfully: {}", userDetails.getUsername());
    return token;
  }

  private Key getSigninKey() {
    byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
    return Keys.hmacShaKeyFor(keyBytes);
  }
}
