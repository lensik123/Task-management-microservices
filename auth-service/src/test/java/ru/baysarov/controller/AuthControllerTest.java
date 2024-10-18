package ru.baysarov.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.baysarov.dto.AuthRequest;
import ru.baysarov.dto.RegisterRequest;
import ru.baysarov.dto.UserDto;
import ru.baysarov.service.AuthService;
import ru.baysarov.service.UserService;

public class AuthControllerTest {

  @InjectMocks
  private AuthController authController;

  @Mock
  private AuthService authService;

  @Mock
  private UserService userService;

  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
  }

  @Test
  void testRegisterUser_Success() throws Exception {
    RegisterRequest registerRequest = new RegisterRequest();
    registerRequest.setFirstName("turpal");
    registerRequest.setLastName("baysarov");
    registerRequest.setEmail("test@bk.ru");
    registerRequest.setPassword("Reyna1998!@");

    mockMvc.perform(post("/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(registerRequest)))
        .andExpect(status().isCreated());

    verify(userService, times(1)).saveUser(any(RegisterRequest.class));
  }

  @Test
  void testRegisterUser_ValidationError() throws Exception {
    RegisterRequest registerRequest = new RegisterRequest();
    registerRequest.setEmail(""); // Invalid email

    mockMvc.perform(post("/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(registerRequest)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void testGetToken_Success() throws Exception {
    AuthRequest authRequest = new AuthRequest();
    authRequest.setEmail("test@bk.ru");
    authRequest.setPassword("password");
    String token = "mocked_token";

    when(authService.authenticateAndReturnToken(any(AuthRequest.class))).thenReturn(token);

    mockMvc.perform(post("/auth/token")
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(authRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.token").value(token));
  }

  @Test
  void testGetToken_ValidationError() throws Exception {
    AuthRequest authRequest = new AuthRequest();
    authRequest.setEmail(""); // Invalid email

    mockMvc.perform(post("/auth/token")
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(authRequest)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void testValidateToken_Success() throws Exception {
    String token = "valid_token";
    UserDto userDto = new UserDto();
    when(authService.validateToken(anyString())).thenReturn(userDto);

    mockMvc.perform(get("/auth/validateToken")
            .param("token", token))
        .andExpect(status().isOk());
  }

  @Test
  void testValidateToken_InvalidToken() throws Exception {
    String token = "invalid_token";
    when(authService.validateToken(anyString())).thenReturn(null);

    mockMvc.perform(get("/auth/validateToken")
            .param("token", token))
        .andExpect(status().isOk());
  }

}
