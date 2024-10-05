package ru.baysarov.task.service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import ru.baysarov.task.service.enums.TaskPriority;
import ru.baysarov.task.service.enums.TaskStatus;

/**
 * Модель задачи, представляющая задачу в системе.
 */
@Entity
@Getter
@Setter
@Table(name = "task")
public class Task {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @NotNull
  private String title;

  @NotNull
  private String description;

  @NotNull
  private Integer authorId;

  private Integer assigneeId;

  private LocalDateTime deadline;

  @Enumerated(EnumType.STRING)
  private TaskStatus status;

  @NotNull(message = "Task priority is required")
  @Enumerated(EnumType.STRING)
  @Column(name = "priority")
  private TaskPriority priority;
}
