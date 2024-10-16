package ru.baysarov.statistic.exception;

/**
 * Исключение, которое выбрасывается, когда задача с указанным идентификатором не найдена.
 */
public class TaskNotFoundException extends RuntimeException {

  private final Integer taskId;

  /**
   * Конструктор, создающий новое исключение с заданным идентификатором задачи.
   *
   * @param taskId идентификатор задачи, которая не была найдена
   */
  public TaskNotFoundException(Integer taskId) {
    super(String.format("Task with ID %d not found", taskId));
    this.taskId = taskId;
  }

  public Integer getTaskId() {
    return taskId;
  }
}
