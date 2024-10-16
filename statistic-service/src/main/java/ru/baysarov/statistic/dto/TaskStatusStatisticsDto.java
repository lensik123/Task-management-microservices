package ru.baysarov.statistic.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class TaskStatusStatisticsDto {
  private long totalTasks;
  private long waitingTasks;
  private long inProcessTasks;
  private long doneTasks;
}
