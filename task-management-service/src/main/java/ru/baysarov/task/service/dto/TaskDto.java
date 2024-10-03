package ru.baysarov.task.service.dto;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;
import ru.baysarov.task.service.enums.TaskPriority;
import ru.baysarov.task.service.enums.TaskStatus;


@Getter
@Setter
public class TaskDto {

  @NotBlank(message = "Task name is required")
  @Size(max = 100, message = "Task name cannot exceed 100 characters")
  @Column(name = "task_name")
  private String title;

  @Size(max = 500, message = "Description cannot exceed 500 characters")
  @Column(name = "description")
  private String description;

  @NotBlank(message = "Assignee ID is required")  // Валидация для assigneeId
  private String assigneeId;

  private LocalDate deadline;

  @NotNull(message = "Task priority is required")
  @Enumerated(EnumType.STRING)
  @Column(name = "priority")
  private TaskPriority priority;

  @Enumerated(EnumType.STRING)
  private TaskStatus status;
}


