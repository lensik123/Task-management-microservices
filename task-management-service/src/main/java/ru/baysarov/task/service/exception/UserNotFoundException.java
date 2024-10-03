package ru.baysarov.task.service.exception;

public class UserNotFoundException extends RuntimeException{

  private final Integer userId;
  public UserNotFoundException(Integer userId) {
    super(String.format("User with ID %d not found", userId));
    this.userId = userId;
  }

  public Integer getUserId() {
    return userId;
  }
}


//package ru.baysarov.task.service.exception;
//
//
//public class TaskNotFoundException extends RuntimeException {
//
//  private final Integer taskId;
//
//  public TaskNotFoundException(Integer taskId) {
//    super(String.format("Task with ID %d not found", taskId));
//    this.taskId = taskId;
//  }
//
//  public Integer getTaskId() {
//    return taskId;
//  }
//}