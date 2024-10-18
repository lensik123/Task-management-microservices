package ru.baysarov.task.service.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.baysarov.task.service.dto.TaskDtoIn;
import ru.baysarov.task.service.dto.TaskDtoOut;
import ru.baysarov.task.service.dto.TaskResponseDto;
import ru.baysarov.task.service.dto.TimeEntryResponseDto;
import ru.baysarov.task.service.dto.UserDto;
import ru.baysarov.task.service.exception.TaskAccessException;
import ru.baysarov.task.service.exception.TaskNotFoundException;
import ru.baysarov.task.service.exception.UserNotFoundException;
import ru.baysarov.task.service.model.Task;
import ru.baysarov.task.service.repository.TaskRepository;
import ru.baysarov.task.service.service.KafkaMessagePublisher;
import ru.baysarov.task.service.service.TaskService;
import ru.baysarov.task.service.service.UserService;

/**
 * Реализация сервиса для управления тасками.
 */
@Service
@Transactional(readOnly = true)
public class TaskServiceImpl implements TaskService {

  private final UserService userService;
  private final TaskRepository taskRepository;
  private final ModelMapper modelMapper;

  private final KafkaMessagePublisher kafkaMessagePublisher;
  private final String taskCreatedTopic = "task_created";
  private final String taskDeletedTopic = "task_deleted";
  private final String taskUpdatedTopic = "task_updated";


  public TaskServiceImpl(UserService userService,
      TaskRepository taskRepository,
      ModelMapper modelMapper,
      KafkaMessagePublisher kafkaMessagePublisher) {
    this.userService = userService;
    this.taskRepository = taskRepository;
    this.modelMapper = modelMapper;
    this.kafkaMessagePublisher = kafkaMessagePublisher;
  }


  /**
   * Создает новую задачу.
   *
   * @param taskDtoIn   объект, содержащий данные о задаче
   * @param authorEmail адрес электронной почты автора задачи
   * @throws UserNotFoundException если пользователь с указанным email не найден
   */
  @Override
  @Transactional
  public void createTask(TaskDtoIn taskDtoIn, String authorEmail) {
    Task task = modelMapper.map(taskDtoIn, Task.class);

    if (!taskDtoIn.getAssigneeEmail().isBlank()) {
      UserDto assignee = userService.getUserByEmail(taskDtoIn.getAssigneeEmail());
      if (assignee == null) {
        throw new UserNotFoundException(
            "Assignee not found for email: " + taskDtoIn.getAssigneeEmail());
      }
      task.setAssigneeId(assignee.getId());
    }

    UserDto author = userService.getUserByEmail(authorEmail);
    if (author == null) {
      throw new UserNotFoundException("Author not found for email: " + authorEmail);
    }
    task.setAuthorId(author.getId());
    taskRepository.save(task);

    TaskDtoOut taskDtoToSend = convertToDtoOut(task);
    kafkaMessagePublisher.sendToTopic(taskCreatedTopic, taskDtoToSend);
  }


  /**
   * Получает задачу по её идентификатору.
   *
   * @param id идентификатор задачи
   * @return объект TaskResponseDto с данными о задаче
   * @throws TaskNotFoundException если задача с указанным идентификатором не найдена
   */
  @Override
  public TaskResponseDto getTaskById(int id) {
    Task task = taskRepository.findById(id)
        .orElseThrow(() -> new TaskNotFoundException(id));

    return convertToResponseDto(task);
  }

  /**
   * Получает список всех задач.
   *
   * @return список объектов TaskDto, представляющих все задачи
   */
  @Override
  public List<TaskDtoOut> getAllTasks(int page, int size) {
    Pageable pageable = PageRequest.of(page, size);
    List<Task> tasks = taskRepository.findAll(pageable).stream().toList();
    return tasks.stream()
        .map(this::convertToDtoOut).collect(Collectors.toList());
  }

  /**
   * Обновляет данные задачи.
   *
   * @param id               идентификатор задачи, которую нужно обновить
   * @param updatedTaskDtoIn объект, содержащий обновленные данные о задаче
   * @throws TaskNotFoundException если задача с указанным идентификатором не найдена
   * @throws UserNotFoundException если пользователь с указанным email не найден
   */
  @Override
  @Transactional
  public void updateTask(int id, TaskDtoIn updatedTaskDtoIn) {
    Task existingTask = taskRepository.findById(id)
        .orElseThrow(() -> new TaskNotFoundException(id));

    existingTask.setTitle(updatedTaskDtoIn.getTitle());
    existingTask.setDescription(updatedTaskDtoIn.getDescription());
    existingTask.setStatus(updatedTaskDtoIn.getStatus());
    if (!updatedTaskDtoIn.getAssigneeEmail().isBlank()) {
      UserDto user = userService.getUserByEmail(updatedTaskDtoIn.getAssigneeEmail());
      existingTask.setAssigneeId(user.getId());
    }
    existingTask.setPriority(updatedTaskDtoIn.getPriority());
    existingTask.setDeadline(updatedTaskDtoIn.getDeadline());

    taskRepository.save(existingTask);

    TaskDtoOut taskDtoToSend = convertToDtoOut(existingTask);
    kafkaMessagePublisher.sendToTopic(taskUpdatedTopic, taskDtoToSend);
  }


  /**
   * Назначает задачу пользователю.
   *
   * @param taskId    идентификатор задачи
   * @param userEmail адрес электронной почты пользователя, которому назначается задача
   * @throws TaskNotFoundException   если задача с указанным идентификатором не найдена
   * @throws UserNotFoundException   если пользователь с указанным email не найден в auth
   * @throws ResponseStatusException иная ошибка при обращении к auth через feign client
   */
  @Override
  @Transactional
  public void assignTask(int taskId, int assigneeId) throws ResponseStatusException {
    Task task = taskRepository.findById(taskId)
        .orElseThrow(() -> new TaskNotFoundException(taskId));

    UserDto user = userService.getUserById(assigneeId);
    task.setAssigneeId(user.getId());
    taskRepository.save(task);

    TaskDtoOut taskDtoToSend = convertToDtoOut(task);
    kafkaMessagePublisher.sendToTopic(taskDeletedTopic, taskDtoToSend);
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

    TaskDtoOut taskDtoToSend = convertToDtoOut(task);
    kafkaMessagePublisher.sendToTopic(taskDeletedTopic, taskDtoToSend);
  }

  /**
   * Устанавливает срок выполнения задачи.
   *
   * @param taskId    идентификатор задачи
   * @param deadLine  срок выполнения задачи
   * @param userEmail адрес электронной почты пользователя, устанавливающего срок
   * @throws TaskNotFoundException если задача с указанным идентификатором не найдена
   * @throws UserNotFoundException если пользователь с указанным email не найден
   * @throws TaskAccessException   если у пользователя нет разрешения на установку срока выполнения
   *                               задачи
   */
  @Override
  @Transactional
  public void setTaskDeadline(int taskId, LocalDateTime deadLine, String userEmail) {
    Task task = taskRepository.findById(taskId)
        .orElseThrow(() -> new TaskNotFoundException(taskId));

    UserDto user = userService.getUserByEmail(userEmail);
    List<String> userRoles = userService.getUserRoles(userEmail);

    if (!task.getAuthorId().equals(user.getId()) && !userRoles.contains("ADMIN")
        && !userRoles.contains("MANAGER")) {
      throw new TaskAccessException(
          "You do not have permission to set the due date for this task.");
    }

    task.setDeadline(deadLine);
    taskRepository.save(task);

    TaskDtoOut taskDtoToSend = convertToDtoOut(task);
    kafkaMessagePublisher.sendToTopic(taskUpdatedTopic, taskDtoToSend);
  }

  /**
   * Преобразует сущность Task в DTO TaskDtoOut, где ID автора и исполнителя (при наличии)
   * конвертируются в их email-ы с использованием userService.
   *
   * @param task объект Task, который необходимо преобразовать
   * @return объект TaskDtoOut, содержащий информацию о задаче и email-ы автора и исполнителя
   * @throws UserNotFoundException если пользователь с указанным ID не найден
   */
  public TaskDtoOut convertToDtoOut(Task task) {
    TaskDtoOut taskDto = modelMapper.map(task, TaskDtoOut.class);

    return taskDto;
  }

  public TaskResponseDto convertToResponseDto(Task task) {
    List<TimeEntryResponseDto> timeEntryResponseDtos = task.getTimeEntries().stream()
        .map(timeEntry -> TimeEntryResponseDto.builder()
            .userId(timeEntry.getUserId())
            .date(timeEntry.getDate())
            .hours(timeEntry.getHours())
            .build())
        .collect(Collectors.toList());

    return TaskResponseDto.builder()
        .id(task.getId())
        .title(task.getTitle())
        .description(task.getDescription())
        .authorId(task.getAuthorId())
        .assigneeId(task.getAssigneeId())
        .deadline(task.getDeadline())
        .createdAt(task.getCreatedAt())
        .updatedAt(task.getUpdatedAt())
        .status(task.getStatus().name()) // Преобразование в строку
        .priority(task.getPriority().name()) // Преобразование в строку
        .timeEntries(timeEntryResponseDtos)
        .build();
  }

}
