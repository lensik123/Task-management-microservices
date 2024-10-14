package ru.baysarov.task.service.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AllTasksResponse {

  private final List<TaskDtoOut> allTasks;

  public AllTasksResponse(List<TaskDtoOut> allTasks) {
    this.allTasks = allTasks;
  }
}
