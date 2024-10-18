package ru.baysarov.task.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.baysarov.task.service.enums.TaskPriority;
import ru.baysarov.task.service.enums.TaskStatus;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class TaskDtoIn {

  @NotBlank(message = "Title is required")
  @Size(max = 100, message = "Task name cannot exceed 100 characters")
  private String title;

  @NotBlank(message = "Description is required")
  @Size(max = 500, message = "Description cannot exceed 500 characters")
  private String description;

  @NotBlank(message = "Assignee email is required")
  private String assigneeEmail;

  @JsonIgnore
  private LocalDateTime deadline;

  @NotNull(message = "Task priority is required")
  private TaskPriority priority;

  private TaskStatus status;

}
