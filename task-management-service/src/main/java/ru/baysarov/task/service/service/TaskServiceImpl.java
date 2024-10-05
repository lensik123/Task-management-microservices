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
import ru.baysarov.task.service.exception.UserNotFoundException;
import ru.baysarov.task.service.feign.UserClient;
import ru.baysarov.task.service.model.Task;
import ru.baysarov.task.service.repository.TaskRepository;

/**
 * Реализация сервиса для управления задачами.
 */
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

  /**
   * Создает новую задачу.
   *
   * @param taskDto объект, содержащий данные о задаче
   * @param authorEmail адрес электронной почты автора задачи
   * @throws UserNotFoundException если пользователь с указанным email не найден
   */
  @Override
  @Transactional
  public void createTask(TaskDto taskDto, String authorEmail) {
    Task task = modelMapper.map(taskDto, Task.class);

    if (!taskDto.getAssigneeEmail().isBlank()) {
      try {
        UserDto assignee = userClient.getUserByEmail(taskDto.getAssigneeEmail()).getBody();
        task.setAssigneeId(assignee.getId());
      } catch (ResponseStatusException e) {
        throw new UserNotFoundException("User " + taskDto.getAssigneeEmail() + " not found");
      }
    }

    try {
      UserDto author = userClient.getUserByEmail(authorEmail).getBody();
      task.setAuthorId(author.getId());
    } catch (ResponseStatusException e) {
      throw new UserNotFoundException("User " + authorEmail + " not found");
    }

    if (taskDto.getStatus() == null) {
      task.setStatus(TaskStatus.WAITING);
    }
    taskRepository.save(task);
  }

  /**
   * Получает задачу по её идентификатору.
   *
   * @param id идентификатор задачи
   * @return объект TaskDto с данными о задаче
   * @throws TaskNotFoundException если задача с указанным идентификатором не найдена
   */
  @Override
  public TaskDto getTaskById(int id) {
    Task task = taskRepository.findById(id)
        .orElseThrow(() -> new TaskNotFoundException(id));
    return modelMapper.map(task, TaskDto.class);
  }

  /**
   * Получает список всех задач.
   *
   * @return список объектов TaskDto, представляющих все задачи
   */
  @Override
  public List<TaskDto> getAllTasks() {
    return taskRepository.findAll().stream()
        .map(task -> modelMapper.map(task, TaskDto.class))
        .collect(Collectors.toList());
  }

  /**
   * Обновляет данные задачи.
   *
   * @param id идентификатор задачи, которую нужно обновить
   * @param updatedTaskDto объект, содержащий обновленные данные о задаче
   * @throws TaskNotFoundException если задача с указанным идентификатором не найдена
   */
  @Override
  @Transactional
  public void updateTask(int id, TaskDto updatedTaskDto) {
    Task updatedTask = modelMapper.map(updatedTaskDto, Task.class);
    updatedTask.setId(id);
    taskRepository.save(updatedTask);
  }

  /**
   * Назначает задачу пользователю.
   *
   * @param taskId идентификатор задачи
   * @param userEmail адрес электронной почты пользователя, которому назначается задача
   * @throws TaskNotFoundException если задача с указанным идентификатором не найдена
   * @throws UserNotFoundException если пользователь с указанным email не найден
   */
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
      throw new UserNotFoundException("User " + userEmail + " not found");
    }
  }

  /**
   * Удаляет задачу по её идентификатору.
   *
   * @param id идентификатор задачи, которую нужно удалить
   * @throws TaskNotFoundException если задача с указанным идентификатором не найдена
   */
  @Override
  @Transactional
  public void deleteTask(int id) {
    Task task = taskRepository.findById(id)
        .orElseThrow(() -> new TaskNotFoundException(id));
    taskRepository.delete(task);
  }

  /**
   * Устанавливает срок выполнения задачи.
   *
   * @param taskId идентификатор задачи
   * @param deadLine срок выполнения задачи
   * @param userEmail адрес электронной почты пользователя, устанавливающего срок
   * @throws TaskNotFoundException если задача с указанным идентификатором не найдена
   * @throws UserNotFoundException если пользователь с указанным email не найден
   * @throws TaskAccessException если у пользователя нет разрешения на установку срока выполнения задачи
   */
  @Override
  @Transactional
  public void setTaskDeadline(int taskId, LocalDateTime deadLine, String userEmail) {
    Task task = taskRepository.findById(taskId)
        .orElseThrow(() -> new TaskNotFoundException(taskId));

    UserDto user = null;
    List<String> userRoles = null;
    try {
      user = userClient.getUserByEmail(userEmail).getBody();
      userRoles = userClient.getUserRoles(userEmail);
    } catch (ResponseStatusException e) {
      throw new UserNotFoundException("User " + userEmail + " not found");
    }

    if (!task.getAuthorId().equals(user.getId()) && !userRoles.contains("ADMIN")
        && !userRoles.contains("TEAM_MODERATOR")) {
      throw new TaskAccessException(
          "You do not have permission to set the due date for this task.");
    }

    task.setDeadline(deadLine);
    taskRepository.save(task);
  }
}
