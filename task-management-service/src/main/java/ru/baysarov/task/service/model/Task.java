package ru.baysarov.task.service.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Task {


  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

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

  @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<TimeEntry> timeEntries;

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
