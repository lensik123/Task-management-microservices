package ru.baysarov.statistic.Model;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "task")
@Getter
@Setter
public class Task {

  @Id
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

  @Column(name = "status")
  private String status;

  @Column(name = "priority")
  private String priority;

  @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<TimeEntry> timeEntries;

}
