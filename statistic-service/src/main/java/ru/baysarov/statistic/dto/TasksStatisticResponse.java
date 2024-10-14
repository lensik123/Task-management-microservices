package ru.baysarov.statistic.dto;

import java.util.List;

public class TasksStatisticResponse {

  private final List<TaskDto> tasksList;

  public TasksStatisticResponse(List<TaskDto> tasksList) {
    this.tasksList = tasksList;
  }
}
