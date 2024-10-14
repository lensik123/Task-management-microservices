package ru.baysarov.task.service.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.server.ResponseStatusException;
import ru.baysarov.task.service.dto.TaskDtoIn;
import ru.baysarov.task.service.dto.TaskDtoOut;
import ru.baysarov.task.service.dto.UserDto;
import ru.baysarov.task.service.enums.TaskStatus;
import ru.baysarov.task.service.exception.TaskAccessException;
import ru.baysarov.task.service.exception.TaskNotFoundException;
import ru.baysarov.task.service.exception.UserNotFoundException;
import ru.baysarov.task.service.model.Task;
import ru.baysarov.task.service.repository.TaskRepository;

/**
 * Реализация сервиса для управления тасками.
 */
@Service
@Transactional(readOnly = true)
public class TaskServiceImpl implements TaskService {

  //TODO: userService сделать вместо userClient

  private final UserService userService;
  private final TaskRepository taskRepository;
  private final ModelMapper modelMapper;
  private final KafkaTemplate<String, Object> kafkaTemplate;
  private final KafkaMessagePublisher kafkaMessagePublisher;

  private final String taskCreatedTopic = "task_created";

  private final String taskDeletedTopic = "task_deleted";

  private final String taskUpdatedTopic = "task_updated";


  public TaskServiceImpl(UserServiceImpl userServiceImpl,
      TaskRepository taskRepository,
      ModelMapper modelMapper, KafkaTemplate<String, Object> kafkaTemplate,
      KafkaMessagePublisher kafkaMessagePublisher) {
    this.userService = userServiceImpl;
    this.taskRepository = taskRepository;
    this.modelMapper = modelMapper;
    this.kafkaTemplate = kafkaTemplate;
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
      task.setAssigneeId(assignee.getId());
    }

    UserDto author = userService.getUserByEmail(authorEmail);
    task.setAuthorId(author.getId());
    modelMapper.map(taskDtoIn, task);

    if (taskDtoIn.getStatus() == null) {
      task.setStatus(TaskStatus.WAITING);
    }
    taskRepository.save(task);

    TaskDtoOut taskDtoOut = convertToDtoOut(task);
    kafkaMessagePublisher.sendTaskToTopic(taskCreatedTopic, taskDtoOut);

  }

  //TODO: добавить сюда вывод исполнителя и автора

  /**
   * Получает задачу по её идентификатору.
   *
   * @param id идентификатор задачи
   * @return объект TaskDto с данными о задаче
   * @throws TaskNotFoundException если задача с указанным идентификатором не найдена
   */
  @Override
  public TaskDtoOut getTaskById(int id) {
    Task task = taskRepository.findById(id)
        .orElseThrow(() -> new TaskNotFoundException(id));

    return convertToDtoOut(task);
  }

  //TODO: Проверить корректность всех выходящих и входящих TASKDTO
  //TODO: продумать сортировку тасков и получение только своих тасков

  /**
   * Получает список всех задач.
   *
   * @return список объектов TaskDto, представляющих все задачи
   */
  @Override
  public List<TaskDtoOut> getAllTasks(int page, int size, boolean isMyTasks) {
    Pageable pageable = PageRequest.of(page, size);
    List<Task> tasks = taskRepository.findAll(pageable).stream().toList();
    return tasks.stream()
        .map(this::convertToDtoOut).collect(Collectors.toList());
  }

  //TODO: доработать taskDTO

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

    TaskDtoOut taskDtoToSend = modelMapper.map(existingTask, TaskDtoOut.class);
    kafkaTemplate.send(taskUpdatedTopic,  taskDtoToSend);
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
  public void assignTask(int taskId, String userEmail) throws ResponseStatusException {
    Task task = taskRepository.findById(taskId)
        .orElseThrow(() -> new TaskNotFoundException(taskId));

    UserDto user = userService.getUserByEmail(userEmail);
    task.setAssigneeId(user.getId());
    taskRepository.save(task);

    TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
      @Override
      public void afterCommit() {
        TaskDtoOut taskDtoToSend = modelMapper.map(task, TaskDtoOut.class);
        kafkaTemplate.send(taskDeletedTopic, "Task deleted", taskDtoToSend);
      }
    });

  }

  /**
   * Удаляет задачу по её идентификатору.
   *
   * @param id идентификатор задачи, которую нужно удалить
   * @throws TaskNotFoundException если задача с указанным идентификатором не найдена
   */
  //TODO: Проверить как exception выбрасывается
  @Override
  @Transactional
  public void deleteTask(int id) {
    Task task = taskRepository.findById(id)
        .orElseThrow(() -> new TaskNotFoundException(id));
    taskRepository.delete(task);

    TaskDtoOut taskDtoToSend = modelMapper.map(task, TaskDtoOut.class);
    kafkaTemplate.send(taskDeletedTopic, "Task deleted", taskDtoToSend);
  }

  //TODO: доработать роль

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
        && !userRoles.contains("TEAM_MODERATOR")) {
      throw new TaskAccessException(
          "You do not have permission to set the due date for this task.");
    }

    task.setDeadline(deadLine);
    taskRepository.save(task);
  }

  /**
   * Маппер из Task в TaskDto. Id автора и Id исполнителя преобразуются в их email-ы.
   *
   * @param task
   * @return объект TaskDtoOut
   */
  public TaskDtoOut convertToDtoOut(Task task) {
    TaskDtoOut taskDto = modelMapper.map(task, TaskDtoOut.class);

    UserDto author = userService.getUserById(task.getAuthorId());
    taskDto.setAuthorEmail(author.getEmail());

    if (task.getAssigneeId() != null) {
      UserDto assignee = userService.getUserById(task.getAssigneeId());
      taskDto.setAssigneeEmail(assignee.getEmail());
    }

    return taskDto;
  }

}
