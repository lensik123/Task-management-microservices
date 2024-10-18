package ru.baysarov.controller;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.baysarov.dto.UserDto;
import ru.baysarov.service.UserService;

public class UserControllerTest {

  private MockMvc mockMvc;

  @Mock
  private UserService userService;

  @InjectMocks
  private UserController userController;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
  }

  @Test
  void testGetUserByEmail_Success() throws Exception {
    String email = "test@bk.ru";
    UserDto userDto = new UserDto();
    userDto.setEmail(email);

    when(userService.findByEmail(email)).thenReturn(userDto);

    mockMvc.perform(get("/user/{email}", email))
        .andExpect(status().isOk());

    verify(userService, times(1)).findByEmail(email);
  }

  @Test
  void testGetUserByEmail_NotFound() throws Exception {
    String email = "notfound@bk.ru";
    when(userService.findByEmail(email)).thenThrow(new RuntimeException("User not found"));

    mockMvc.perform(get("/user/{email}", email))
        .andExpect(status().isNotFound());

    verify(userService, times(1)).findByEmail(email);
  }

  @Test
  void testGetUserById_Success() throws Exception {
    int id = 1;
    UserDto userDto = new UserDto();
    userDto.setId(id);

    when(userService.findById(id)).thenReturn(userDto);

    mockMvc.perform(get("/user/id/{id}", id))
        .andExpect(status().isOk());

    verify(userService, times(1)).findById(id);
  }

  @Test
  void testGetUserById_NotFound() throws Exception {
    int id = 999;
    when(userService.findById(id)).thenThrow(new RuntimeException("User not found"));

    mockMvc.perform(get("/user/id/{id}", id))
        .andExpect(status().isNotFound());

    verify(userService, times(1)).findById(id);
  }

  @Test
  void testGetUserRoles_Success() throws Exception {
    String email = "test@bk.ru";
    when(userService.getUserRoles(email)).thenReturn(List.of("USER", "ADMIN"));

    mockMvc.perform(get("/user/{email}/roles", email))
        .andExpect(status().isOk());

    verify(userService, times(1)).getUserRoles(email);
  }
}
