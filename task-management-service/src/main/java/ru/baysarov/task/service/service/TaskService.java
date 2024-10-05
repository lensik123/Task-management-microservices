package ru.baysarov.task.service.service;

import java.time.LocalDateTime;
import java.util.List;
import ru.baysarov.task.service.dto.TaskDto;
import ru.baysarov.task.service.exception.TaskAccessException;
import ru.baysarov.task.service.exception.TaskNotFoundException;
import ru.baysarov.task.service.exception.UserNotFoundException;

/**
 * Интерфейс для управления задачами в системе.
 */
public interface TaskService {

  /**
   * Создает новую задачу.
   *
   * @param taskDto объект, содержащий данные о задаче
   * @param authorEmail адрес электронной почты автора задачи
   */
  void createTask(TaskDto taskDto, String authorEmail);

  /**
   * Получает задачу по её идентификатору.
   *
   * @param id идентификатор задачи
   * @return объект TaskDto с данными о задаче
   */
  TaskDto getTaskById(int id);

  /**
   * Получает список всех задач.
   *
   * @return список объектов TaskDto, представляющих все задачи
   */
  List<TaskDto> getAllTasks();

  /**
   * Обновляет данные задачи.
   *
   * @param id идентификатор задачи, которую нужно обновить
   * @param updatedTask объект, содержащий обновленные данные о задаче
   */
  void updateTask(int id, TaskDto updatedTask);

  /**
   * Назначает задачу пользователю.
   *
   * @param taskId идентификатор задачи
   * @param userEmail адрес электронной почты пользователя, которому назначается задача
   */
  void assignTask(int taskId, String userEmail);

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
