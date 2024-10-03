package ru.baysarov.task.service.exception;


public class TaskNotFoundException extends RuntimeException {

  private final Integer taskId;

  public TaskNotFoundException(Integer taskId) {
    super(String.format("Task with ID %d not found", taskId));
    this.taskId = taskId;
  }

  public Integer getTaskId() {
    return taskId;
  }
}
