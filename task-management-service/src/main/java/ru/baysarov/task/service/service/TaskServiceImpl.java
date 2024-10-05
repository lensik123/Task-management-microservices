package ru.baysarov.task.service.service;


import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.baysarov.task.service.dto.TaskDto;
import ru.baysarov.task.service.dto.UserDto;
import ru.baysarov.task.service.enums.TaskStatus;
import ru.baysarov.task.service.exception.TaskAccessException;
import ru.baysarov.task.service.exception.TaskNotFoundException;
import ru.baysarov.task.service.feign.UserClient;
import ru.baysarov.task.service.model.Task;
import ru.baysarov.task.service.repository.TaskRepository;


//TODO: Сделать чтобы методы возвращали объект
@Service
@Transactional(readOnly = true)
public class TaskServiceImpl implements TaskService {


  private final UserClient userClient;
  private final TaskRepository taskRepository;
  private final ModelMapper modelMapper;

  public TaskServiceImpl(UserClient userClient, TaskRepository taskRepository,
      ModelMapper modelMapper) {
    this.userClient = userClient;
    this.taskRepository = taskRepository;
    this.modelMapper = modelMapper;
  }


  //TODO: добавить исполнителя
  @Override
  @Transactional
  public void createTask(TaskDto taskDto, String userEmail) {
    Task task = modelMapper.map(taskDto, Task.class);

    if (!taskDto.getAssigneeEmail().isBlank()) {
      try {
        UserDto user = userClient.getUserByEmail(userEmail).getBody();
        task.setAuthorId(user.getId());
      } catch (ResponseStatusException e) {
        throw e;
      }
    }

    if (taskDto.getStatus() == null) {
      task.setStatus(TaskStatus.WAITING);
    }
    taskRepository.save(task);
  }

  @Override
  public TaskDto getTaskById(int id) {
    Task task = taskRepository.findById(id)
        .orElseThrow(() -> new TaskNotFoundException(id));
    return modelMapper.map(task, TaskDto.class);
  }

  @Override
  public List<TaskDto> getAllTasks() {
    return taskRepository.findAll().stream()
        .map(task -> modelMapper.map(task, TaskDto.class))
        .collect(Collectors.toList());
  }

  @Override
  @Transactional
  public void updateTask(int id, TaskDto updatedTaskDto) {
    Task updatedTask = modelMapper.map(updatedTaskDto, Task.class);
    updatedTask.setId(id);
    taskRepository.save(updatedTask);
  }

  @Override
  @Transactional
  public void assignTask(int taskId, String userEmail) {
    Task task = taskRepository.findById(taskId)
        .orElseThrow(() -> new TaskNotFoundException(taskId));

    try {
      UserDto user = userClient.getUserByEmail(userEmail).getBody();
      task.setAssigneeId(user.getId());
      taskRepository.save(task);
    } catch (ResponseStatusException e) {
      throw e;
    }
  }

  @Override
  @Transactional
  public void deleteTask(int id) {
    Task task = taskRepository.findById(id)
        .orElseThrow(() -> new TaskNotFoundException(id));
    taskRepository.delete(task);
  }

  //TODO: почему при изменении роли у юзера в бд -
  @Override
  @Transactional
  public TaskDto setTaskDeadline(int taskId, LocalDateTime deadLine, String userEmail) {
    Task task = taskRepository.findById(taskId)
        .orElseThrow(() -> new TaskNotFoundException(taskId));

    UserDto user = null;
    List<String> userRoles = null;
    try {
      user = userClient.getUserByEmail(userEmail).getBody();
      userRoles = userClient.getUserRoles(userEmail);
    } catch (ResponseStatusException e) {
      System.out.println(e.getMessage());
      throw e;
    }

    if (!task.getAuthorId().equals(user.getId()) && !userRoles.contains("ADMIN")
        && !userRoles.contains("TEAM_MODERATOR")) {
      throw new TaskAccessException(
          "You do not have permission to set the due date for this task.");
    }

    task.setDeadline(deadLine);
    Task savedTask = taskRepository.save(task);
    return modelMapper.map(savedTask, TaskDto.class);
  }


}
