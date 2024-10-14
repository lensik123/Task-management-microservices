package ru.baysarov.task.service.dto;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.baysarov.task.service.enums.TaskPriority;
import ru.baysarov.task.service.enums.TaskStatus;

@Getter
@Setter
@ToString
public class TaskDtoOut {

  private Integer id;
  private String title;
  private String description;
  private String assigneeEmail;
  private String authorEmail;
  private LocalDateTime deadline;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private TaskPriority priority;
  private TaskStatus status;
  private Double timeSpentHours;
}
