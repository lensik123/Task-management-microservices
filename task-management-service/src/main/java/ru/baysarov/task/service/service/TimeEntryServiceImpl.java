package ru.baysarov.task.service.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.baysarov.task.service.dto.TimeEntryDtoIn;
import ru.baysarov.task.service.dto.TimeEntryDtoOut;
import ru.baysarov.task.service.dto.UserDto;
import ru.baysarov.task.service.exception.TaskNotFoundException;
import ru.baysarov.task.service.feign.UserClient;
import ru.baysarov.task.service.model.Task;
import ru.baysarov.task.service.model.TimeEntry;
import ru.baysarov.task.service.repository.TaskRepository;
import ru.baysarov.task.service.repository.TimeEntryRepository;

/**
 * Реализация сервиса для работы с записями времени.
 *
 * <p>Сервис управляет записями времени, связанными с задачами, включая их сохранение
 * и отправку информации о них в Kafka.</p>
 */
@Service
@Transactional(readOnly = true)
public class TimeEntryServiceImpl implements TimeEntryService {

  private final TimeEntryRepository timeEntryRepository;
  private final TaskRepository taskRepository;
  private final UserService userService;
  private final KafkaMessagePublisher kafkaMessagePublisher;
  private final String timeEntryTopic = "time_entry";

  /**
   * Конструктор для создания экземпляра TimeEntryServiceImpl.
   *
   * @param timeEntryRepository репозиторий для работы с записями времени
   * @param taskRepository репозиторий для работы с задачами
   * @param userClient клиент для работы с пользователями
   * @param userService сервис для работы с пользователями
   * @param kafkaMessagePublisher сервис для отправки сообщений в Kafka
   */
  public TimeEntryServiceImpl(TimeEntryRepository timeEntryRepository,
      TaskRepository taskRepository,
      UserClient userClient,
      UserService userService,
      KafkaMessagePublisher kafkaMessagePublisher) {
    this.timeEntryRepository = timeEntryRepository;
    this.taskRepository = taskRepository;
    this.userService = userService;
    this.kafkaMessagePublisher = kafkaMessagePublisher;
  }

  /**
   * Сохраняет запись времени для заданной задачи и отправляет её в Kafka.
   *
   * @param taskId идентификатор задачи, к которой относится запись времени
   * @param userEmail электронная почта пользователя, создавшего запись
   * @param timeEntryDtoIn объект, содержащий данные записи времени
   * @throws TaskNotFoundException если задача с указанным идентификатором не найдена
   */
  @Override
  @Transactional
  public void saveTimeEntry(Integer taskId, String userEmail, TimeEntryDtoIn timeEntryDtoIn) {
    Task task = taskRepository.findById(taskId)
        .orElseThrow(() -> new TaskNotFoundException(taskId));

    TimeEntry timeEntry = new TimeEntry();
    timeEntry.setTask(task);

    UserDto user = userService.getUserByEmail(userEmail);
    timeEntry.setUserId(user.getId());
    timeEntry.setDate(timeEntryDtoIn.getDate());
    timeEntry.setHours(timeEntryDtoIn.getHours());

    timeEntryRepository.save(timeEntry);

    TimeEntryDtoOut timeEntryDtoOut = convertToDto(timeEntry);
    kafkaMessagePublisher.sendToTopic(timeEntryTopic, timeEntryDtoOut);
  }

  /**
   * Преобразует объект TimeEntry в объект TimeEntryDtoOut.
   *
   * @param timeEntry объект записи времени, который необходимо преобразовать
   * @return объект TimeEntryDtoOut, содержащий данные записи времени
   */
  TimeEntryDtoOut convertToDto(TimeEntry timeEntry) {
    TimeEntryDtoOut dto = new TimeEntryDtoOut();
    dto.setId(timeEntry.getId());
    dto.setDate(timeEntry.getDate());
    dto.setHours(timeEntry.getHours());
    dto.setTaskId(timeEntry.getTask().getId());
    dto.setUserId(timeEntry.getUserId());
    return dto;
  }
}
