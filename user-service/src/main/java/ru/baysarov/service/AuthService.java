package ru.baysarov.service;


import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.baysarov.dto.AuthRequest;
import ru.baysarov.dto.UserDto;
import ru.baysarov.repository.UserRepository;

@Service
@Transactional
public class AuthService {

  private final AuthenticationManager authenticationManager;
  private final UserRepository userRepository;

  @Value("${jwt.secret}")
  private String SECRET_KEY;

  @Value("${jwt.expiration-time}")
  private long expirationTimeInSeconds;

  @Autowired
  public AuthService(AuthenticationManager authenticationManager, UserRepository userRepository) {
    this.authenticationManager = authenticationManager;
    this.userRepository = userRepository;
  }

  private Key getSigninKey() {
    byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
    return Keys.hmacShaKeyFor(keyBytes);
  }

  public String generateToken(String email) {
    long expirationTimeInMillis = expirationTimeInSeconds * 1000;
    return Jwts.builder()
        .setSubject(email)
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + expirationTimeInMillis)) // 24 часа
        .signWith(getSigninKey(), SignatureAlgorithm.HS256)
        .compact();
  }

  public UserDto validateToken(String token) {
    String email = Jwts.parserBuilder()
        .setSigningKey(getSigninKey())
        .build()
        .parseClaimsJws(token)
        .getBody()
        .getSubject();

    return userRepository.findByEmail(email)
        .map(user -> new UserDto(user.getId(), user.getEmail()))
        .orElseThrow(() -> new RuntimeException("User not found"));
  }

  public String authenticateAndReturnToken(AuthRequest request) {
    var authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
    );

    var userDetails = (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
    return generateToken(userDetails.getUsername());
  }
}

