package ru.baysarov.task.service.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.baysarov.task.service.dto.TaskDtoIn;
import ru.baysarov.task.service.dto.TaskDtoOut;
import ru.baysarov.task.service.dto.UserDto;
import ru.baysarov.task.service.enums.TaskPriority;
import ru.baysarov.task.service.enums.TaskStatus;
import ru.baysarov.task.service.exception.TaskAccessException;
import ru.baysarov.task.service.exception.TaskNotFoundException;
import ru.baysarov.task.service.model.Task;
import ru.baysarov.task.service.repository.TaskRepository;
import ru.baysarov.task.service.service.KafkaMessagePublisher;
import ru.baysarov.task.service.service.UserService;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {


  @Mock
  private UserService userService;

  @Mock
  private TaskRepository taskRepository;

  @Mock
  private ModelMapper modelMapper;
  @Mock
  private KafkaMessagePublisher kafkaMessagePublisher;
  @InjectMocks
  private TaskServiceImpl taskService;

  private Task task;
  private TaskDtoIn taskDtoIn;
  private TaskDtoOut taskDtoOut;

  @BeforeEach
  public void setUp() {
    task = new Task();
    task.setId(1);
    task.setTitle("Test Task");
    task.setDescription("Test Description");
    task.setStatus(TaskStatus.WAITING);
    task.setAssigneeId(2);
    task.setAuthorId(3);
    task.setDeadline(LocalDateTime.now().plusDays(5));
    task.setPriority(TaskPriority.MEDIUM);

    taskDtoIn = new TaskDtoIn("Updated Task", "Updated Description", "assignee@test.com",
        LocalDateTime.now(), TaskPriority.MEDIUM, TaskStatus.DONE);
    taskDtoOut = new TaskDtoOut();
    taskDtoOut.setId(task.getId());
    taskDtoOut.setTitle(task.getTitle());
    taskDtoOut.setDescription(task.getDescription());
    taskDtoOut.setStatus(task.getStatus());
    taskDtoOut.setPriority(task.getPriority());
    taskDtoOut.setAssigneeId(task.getAssigneeId());
    taskDtoOut.setAuthorId(task.getAuthorId());
    taskDtoOut.setDeadline(task.getDeadline());
    taskDtoOut.setCreatedAt(LocalDateTime.now());
    taskDtoOut.setUpdatedAt(LocalDateTime.now());
  }

  @Test
  void createTask_ShouldCreateTask_WhenValidData() {
    when(userService.getUserByEmail("assignee@test.com")).thenReturn(
        new UserDto(2, "assignee@test.com"));
    when(userService.getUserByEmail("author@test.com")).thenReturn(
        new UserDto(3, "author@test.com"));
    when(modelMapper.map(taskDtoIn, Task.class)).thenReturn(task);
    when(taskRepository.save(task)).thenReturn(task);
    when(modelMapper.map(task, TaskDtoOut.class)).thenReturn(taskDtoOut);

    taskService.createTask(taskDtoIn, "author@test.com");

    verify(taskRepository, times(1)).save(task);
    verify(kafkaMessagePublisher, times(1)).sendToTopic("task_created", taskDtoOut);
  }

  @Test
  void getTaskById_ShouldReturnTask_WhenTaskExists() {
    when(taskRepository.findById(1)).thenReturn(Optional.of(task));
    when(modelMapper.map(task, TaskDtoOut.class)).thenReturn(taskDtoOut);

    TaskDtoOut result = taskService.getTaskById(1);

    assertEquals(taskDtoOut, result);
  }

  @Test
  void getTaskById_ShouldThrowTaskNotFoundException_WhenTaskDoesNotExist() {
    when(taskRepository.findById(1)).thenReturn(Optional.empty());

    assertThrows(TaskNotFoundException.class, () -> taskService.getTaskById(1));
  }

  @Test
  void getAllTasks_ShouldReturnListOfTasks() {
    List<Task> tasks = List.of(task);
    Page<Task> taskPage = new PageImpl<>(tasks);

    when(taskRepository.findAll(PageRequest.of(0, 10))).thenReturn(taskPage);

    List<TaskDtoOut> result = taskService.getAllTasks(0, 10);

    assertEquals(1, result.size());
  }

  @Test
  void updateTask_ShouldUpdateTask_WhenValidData() {
    when(taskRepository.findById(1)).thenReturn(Optional.of(task));
    when(userService.getUserByEmail("assignee@test.com")).thenReturn(
        new UserDto(2, "assignee@test.com"));

    taskService.updateTask(1, taskDtoIn);

    assertEquals("Updated Task", task.getTitle());
    assertEquals("Updated Description", task.getDescription());
    assertEquals(TaskStatus.DONE, task.getStatus());
    verify(taskRepository, times(1)).save(task);
  }

  @Test
  void updateTask_ShouldThrowTaskNotFoundException_WhenTaskDoesNotExist() {
    when(taskRepository.findById(1)).thenReturn(Optional.empty());

    assertThrows(TaskNotFoundException.class, () -> taskService.updateTask(1, taskDtoIn));
  }

  @Test
  void deleteTask_ShouldDeleteTask_WhenTaskExists() {
    when(taskRepository.findById(1)).thenReturn(Optional.of(task));
    when(modelMapper.map(task, TaskDtoOut.class)).thenReturn(taskDtoOut);

    taskService.deleteTask(1);

    verify(taskRepository, times(1)).delete(task);
  }

  @Test
  void deleteTask_ShouldThrowTaskNotFoundException_WhenTaskDoesNotExist() {
    when(taskRepository.findById(1)).thenReturn(Optional.empty());

    assertThrows(TaskNotFoundException.class, () -> taskService.deleteTask(1));
  }

  @Test
  void setTaskDeadline_ShouldSetDeadline_WhenValidData() {
    LocalDateTime deadline = LocalDateTime.now().plusDays(3);
    when(taskRepository.findById(1)).thenReturn(Optional.of(task));
    when(userService.getUserByEmail("author@test.com")).thenReturn(
        new UserDto(3, "author@test.com"));

    taskService.setTaskDeadline(1, deadline, "author@test.com");

    assertEquals(deadline, task.getDeadline());
    verify(taskRepository, times(1)).save(task);
  }

  @Test
  void setTaskDeadline_ShouldThrowTaskNotFoundException_WhenTaskDoesNotExist() {
    when(taskRepository.findById(1)).thenReturn(Optional.empty());

    assertThrows(TaskNotFoundException.class,
        () -> taskService.setTaskDeadline(1, LocalDateTime.now(), "author@test.com"));
  }

  @Test
  void setTaskDeadline_ShouldThrowTaskAccessException_WhenUserDoesNotHavePermission() {
    when(taskRepository.findById(1)).thenReturn(Optional.of(task));
    when(userService.getUserByEmail("user@test.com")).thenReturn(new UserDto(4, "user@test.com"));
    when(userService.getUserRoles("user@test.com")).thenReturn(List.of("USER"));

    assertThrows(TaskAccessException.class,
        () -> taskService.setTaskDeadline(1, LocalDateTime.now(), "user@test.com"));
  }
}
