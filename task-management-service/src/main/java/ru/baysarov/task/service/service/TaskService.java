package ru.baysarov.task.service.service;

import java.time.LocalDateTime;
import java.util.List;
import ru.baysarov.task.service.dto.TaskDtoIn;
import ru.baysarov.task.service.dto.TaskDtoOut;
import ru.baysarov.task.service.dto.TaskResponseDto;

/**
 * Интерфейс для управления задачами в системе.
 */
public interface TaskService {

  /**
   * Создает новую задачу.
   *
   * @param taskDtoIn   объект, содержащий данные о задаче
   * @param authorEmail адрес электронной почты автора задачи
   */
  void createTask(TaskDtoIn taskDtoIn, String authorEmail);

  /**
   * Получает задачу по её идентификатору.
   *
   * @param id идентификатор задачи
   * @return объект TaskResponseDto с данными о задаче
   */
  TaskResponseDto getTaskById(int id);


  /**
   * Получает список всех задач.
   *
   * @return список объектов TaskDto, представляющих все задачи
   */
  List<TaskDtoOut> getAllTasks(int page, int size);

  /**
   * Обновляет данные задачи.
   *
   * @param id          идентификатор задачи, которую нужно обновить
   * @param updatedTask объект, содержащий обновленные данные о задаче
   */
  void updateTask(int id, TaskDtoIn updatedTask);

  /**
   * Назначает задачу пользователю.
   *
   * @param taskId    идентификатор задачи
   * @param userEmail адрес электронной почты пользователя, которому назначается задача
   */
  void assignTask(int taskId, int assigneeId);

  /**
   * Удаляет задачу по её идентификатору.
   *
   * @param id идентификатор задачи, которую нужно удалить
   */
  void deleteTask(int id);

  /**
   * Устанавливает срок выполнения задачи.
   *
   * @param taskId    идентификатор задачи
   * @param deadLine  срок выполнения задачи
   * @param userEmail адрес электронной почты пользователя, устанавливающего срок
   */
  void setTaskDeadline(int taskId, LocalDateTime deadLine, String userEmail);
}
