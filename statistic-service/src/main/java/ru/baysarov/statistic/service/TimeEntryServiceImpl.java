package ru.baysarov.statistic.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.baysarov.statistic.Model.Task;
import ru.baysarov.statistic.Model.TimeEntry;
import ru.baysarov.statistic.Repostiory.TaskRepository;
import ru.baysarov.statistic.Repostiory.TimeEntryRepository;
import ru.baysarov.statistic.dto.TimeEntryDto;
import ru.baysarov.statistic.exception.TaskNotFoundException;

/**
 * Реализация сервиса для работы с записями о затраченном времени на задачу.
 * Обрабатывает операции сохранения записей о времени, связывая их с задачами.
 */
@Service
@Transactional(readOnly = true)
public class TimeEntryServiceImpl implements TimeEntryService {

  private final TimeEntryRepository timeEntryRepository;
  private final TaskRepository taskRepository;


  public TimeEntryServiceImpl(TimeEntryRepository timeEntryRepository,
      TaskRepository taskRepository) {
    this.timeEntryRepository = timeEntryRepository;
    this.taskRepository = taskRepository;
  }

  /**
   * Сохраняет запись о времени, связанной с задачей.
   *
   * @param timeEntryDto объект записи о времени, содержащий информацию о задаче, пользователе и затраченном времени
   * @throws TaskNotFoundException если задача с указанным идентификатором не найдена
   */
  @Override
  @Transactional
  public void saveTimeEntry(TimeEntryDto timeEntryDto) {
    Task task = taskRepository.findById(timeEntryDto.getTaskId())
        .orElseThrow(
            () -> new TaskNotFoundException("Task not found: " + timeEntryDto.getTaskId()));

    TimeEntry timeEntry = new TimeEntry();
    timeEntry.setTask(task);
    timeEntry.setUserId(timeEntryDto.getUserId());
    timeEntry.setDate(timeEntryDto.getDate());
    timeEntry.setHours(timeEntryDto.getHours());

    timeEntryRepository.save(timeEntry);
  }
}
