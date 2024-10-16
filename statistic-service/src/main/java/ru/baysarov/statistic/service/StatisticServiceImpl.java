package ru.baysarov.statistic.service;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.baysarov.statistic.Model.TimeEntry;
import ru.baysarov.statistic.Repostiory.TaskRepository;
import ru.baysarov.statistic.Repostiory.TimeEntryRepository;
import ru.baysarov.statistic.dto.TaskStatusStatisticsDto;
import ru.baysarov.statistic.dto.UserDto;
import ru.baysarov.statistic.feign.UserClient;

/**
 * Реализация сервиса статистики, предоставляющего отчеты о затраченном времени
 * и статистику по статусам задач.
 */
@Service
@Transactional(readOnly = true)
public class StatisticServiceImpl implements StatisticService {

  private final TaskRepository taskRepository;
  private final TimeEntryRepository timeEntryRepository;
  private final UserClient userClient;

  /**
   * Конструктор класса StatisticServiceImpl.
   *
   * @param taskRepository репозиторий задач
   * @param timeEntryRepository репозиторий временных записей
   * @param userClient клиент для взаимодействия с пользователями
   */
  public StatisticServiceImpl(TaskRepository taskRepository,
      TimeEntryRepository timeEntryRepository, UserClient userClient) {
    this.taskRepository = taskRepository;
    this.timeEntryRepository = timeEntryRepository;
    this.userClient = userClient;
  }

  /**
   * Генерирует отчет о затраченном времени за указанный период.
   *
   * @param userEmail электронная почта пользователя, запрашивающего отчет
   * @param startDate дата начала периода
   * @param endDate дата окончания периода
   * @param isManager флаг, указывающий, является ли пользователь менеджером
   * @return отчет в виде ByteArrayInputStream
   */
  @Override
  public ByteArrayInputStream timeSpentReport(String userEmail, LocalDate startDate, LocalDate endDate, boolean isManager) {
    List<TimeEntry> entries;

    if (isManager) {
      entries = timeEntryRepository.findByDateBetween(startDate, endDate);
    } else {
      UserDto userDto = userClient.getUserByEmail(userEmail).getBody();
      entries = timeEntryRepository.findByUserIdAndDateBetween(userDto.getId(), startDate, endDate);
    }

    return createCsvReport(entries);
  }

  /**
   * Создает CSV отчет на основе списка временных записей.
   *
   * @param entries список временных записей
   * @return отчет в виде ByteArrayInputStream
   */
  private ByteArrayInputStream createCsvReport(List<TimeEntry> entries) {
    StringBuilder csvBuilder = new StringBuilder();
    csvBuilder.append("date,hours,user_id,task_id\n");

    for (TimeEntry entry : entries) {
      csvBuilder.append(entry.getDate())
          .append(",")
          .append(entry.getHours())
          .append(",")
          .append(entry.getUserId())
          .append(",")
          .append(entry.getTask().getId())
          .append("\n");
    }

    return new ByteArrayInputStream(csvBuilder.toString().getBytes());
  }

  /**
   * Получает статистику по всем статусам задач.
   *
   * @return объект с информацией о статусах задач
   */
  @Override
  public TaskStatusStatisticsDto allTasksStatuses() {
    TaskStatusStatisticsDto dto = taskRepository.getTaskStatusStatistics();
    return dto;
  }
}
