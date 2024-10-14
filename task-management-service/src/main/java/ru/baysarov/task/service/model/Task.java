package ru.baysarov.task.service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import ru.baysarov.task.service.enums.TaskPriority;
import ru.baysarov.task.service.enums.TaskStatus;

/**
 * Модель таска, представляющая таск в системе.
 */
@Entity
@Getter
@Setter
@Table(name = "task")
public class Task {


  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  //TODO: varchar 255 to varchar 100
  @Column(name = "title")
  private String title;

  @Column(name = "description")
  private String description;

  @Column(name = "author_id")
  private Integer authorId;

  @Column(name = "assignee_id")
  private Integer assigneeId;

  @Column(name = "deadline")
  private LocalDateTime deadline;

  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  @Enumerated(EnumType.STRING)
  private TaskStatus status;

  @Enumerated(EnumType.STRING)
  @Column(name = "priority")
  private TaskPriority priority;

  @Column(name = "time_spent_hours")
  private Double timeSpentHours;

  @PrePersist
  protected void onCreate() {
    this.createdAt = LocalDateTime.now();
    this.updatedAt = LocalDateTime.now();
  }

  @PreUpdate
  protected void onUpdate() {
    this.updatedAt = LocalDateTime.now();
  }
}
