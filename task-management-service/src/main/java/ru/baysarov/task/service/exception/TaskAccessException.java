package ru.baysarov.task.service.exception;

public class TaskAccessException extends RuntimeException{

  public TaskAccessException(String message) {
    super(message);
  }
}
