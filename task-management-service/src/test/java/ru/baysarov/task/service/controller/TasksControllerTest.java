package ru.baysarov.task.service.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.baysarov.task.service.dto.SetDeadlineRequest;
import ru.baysarov.task.service.dto.TaskDtoIn;
import ru.baysarov.task.service.dto.TaskDtoOut;
import ru.baysarov.task.service.enums.TaskPriority;
import ru.baysarov.task.service.enums.TaskStatus;
import ru.baysarov.task.service.service.TaskService;

@ExtendWith(MockitoExtension.class)
class TasksControllerTest {

  @Mock
  private TaskService taskService;

  @InjectMocks
  private TasksController tasksController;

  private MockMvc mockMvc;
  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(tasksController).build();
    objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
  }

  @Test
  void getAllTasks() throws Exception {
    List<TaskDtoOut> taskList = new ArrayList<>();
    for (int i = 0; i < 5; i++) {
      TaskDtoOut taskDtoOut = new TaskDtoOut();
      taskDtoOut.setId(i + 1);
      taskDtoOut.setTitle("Task " + (i + 1));
      taskDtoOut.setDescription("Description for Task " + (i + 1));
      taskDtoOut.setAssigneeId(i + 1);
      taskDtoOut.setAuthorId(i + 1);
      taskDtoOut.setDeadline(LocalDateTime.now().plusDays(5));
      taskDtoOut.setCreatedAt(LocalDateTime.now());
      taskDtoOut.setUpdatedAt(LocalDateTime.now());
      taskDtoOut.setPriority(TaskPriority.MEDIUM);
      taskDtoOut.setStatus(TaskStatus.IN_PROCESS);
      taskList.add(taskDtoOut);
    }

    when(taskService.getAllTasks(0, 10)).thenReturn(taskList);

    mockMvc.perform(get("/api/v1/tasks"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.allTasks", hasSize(5)))
        .andExpect(jsonPath("$.allTasks[0].title").value("Task 1"));
    verify(taskService, times(1)).getAllTasks(0, 10);
  }

  @Test
  void createTask() throws Exception {
    TaskDtoIn taskDtoIn = new TaskDtoIn();
    taskDtoIn.setTitle("Task 1");
    taskDtoIn.setDescription("Description for Task 1");
    taskDtoIn.setAssigneeEmail("test@bk.ru");
    taskDtoIn.setPriority(TaskPriority.MEDIUM);

    String taskJson = objectMapper.writeValueAsString(taskDtoIn);

    mockMvc.perform(post("/api/v1/tasks")
            .content(taskJson)
            .contentType("application/json")
            .header("X-auth-user-email", "test@bk.ru"))  // Добавляем заголовок
        .andExpect(status().isCreated());
    verify(taskService, times(1)).createTask(taskDtoIn, "test@bk.ru");
  }

  @Test
  void getTaskById() throws Exception {
    TaskDtoOut taskDtoOut = new TaskDtoOut();
    taskDtoOut.setId(1);
    taskDtoOut.setTitle("Task 1");
    taskDtoOut.setDescription("Description for Task 1");
    taskDtoOut.setAssigneeId(1);
    taskDtoOut.setAuthorId(1);
    taskDtoOut.setDeadline(LocalDateTime.now().plusDays(5));
    taskDtoOut.setCreatedAt(LocalDateTime.now());
    taskDtoOut.setUpdatedAt(LocalDateTime.now());
    taskDtoOut.setPriority(TaskPriority.MEDIUM);
    taskDtoOut.setStatus(TaskStatus.IN_PROCESS);

    when(taskService.getTaskById(1)).thenReturn(taskDtoOut);

    mockMvc.perform(get("/api/v1/tasks/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.title").value("Task 1"))
        .andExpect(jsonPath("$.description").value("Description for Task 1"))
        .andExpect(jsonPath("$.priority").value("MEDIUM"))
        .andExpect(jsonPath("$.status").value("IN_PROCESS"));

    verify(taskService, times(1)).getTaskById(1);
  }

  @Test
  void deleteTaskById() throws Exception {
    mockMvc.perform(delete("/api/v1/tasks/1"))
        .andExpect(status().isOk());

    verify(taskService, times(1)).deleteTask(1);
  }

  @Test
  void updateTask() throws Exception {
    TaskDtoIn taskDtoIn = new TaskDtoIn();
    taskDtoIn.setTitle("Updated Task");
    taskDtoIn.setDescription("Updated Description");
    taskDtoIn.setPriority(TaskPriority.HIGH);
    taskDtoIn.setStatus(TaskStatus.DONE);
    taskDtoIn.setAssigneeEmail("assignee@test.com");

    String taskJson = objectMapper.writeValueAsString(taskDtoIn);

    mockMvc.perform(put("/api/v1/tasks/1")
            .contentType("application/json")
            .content(taskJson))
        .andExpect(status().isOk());

    verify(taskService, times(1)).updateTask(eq(1), eq(taskDtoIn));
  }

  @Test
  void setTaskDeadline() throws Exception {
    SetDeadlineRequest deadlineRequest = new SetDeadlineRequest();
    deadlineRequest.setDeadline(LocalDateTime.now().plusDays(5));

    String requestJson = objectMapper.writeValueAsString(deadlineRequest);

    mockMvc.perform(patch("/api/v1/tasks/1/deadline")
            .contentType("application/json")
            .content(requestJson)
            .header("X-auth-user-email", "user@test.com"))
        .andExpect(status().isOk());

    verify(taskService, times(1)).setTaskDeadline(eq(1), eq(deadlineRequest.getDeadline()), eq("user@test.com"));
  }





}
