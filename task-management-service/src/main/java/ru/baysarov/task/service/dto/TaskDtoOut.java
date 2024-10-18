package ru.baysarov.task.service.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.baysarov.task.service.enums.TaskPriority;
import ru.baysarov.task.service.enums.TaskStatus;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskDtoOut {

  private Integer id;
  private String title;
  private String description;
  private Integer assigneeId;
  private Integer authorId;
  private LocalDateTime deadline;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private TaskPriority priority;
  private TaskStatus status;
}
