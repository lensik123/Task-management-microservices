package ru.baysarov.statistic.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class TaskStatusStatisticsDto {

  private long totalTasks;
  private long waitingTasks;
  private long inProcessTasks;
  private long doneTasks;
}
