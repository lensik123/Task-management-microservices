package ru.baysarov.task.service.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskResponseDto {

  private Integer id;
  private String title;
  private String description;
  private Integer authorId;
  private Integer assigneeId;
  private LocalDateTime deadline;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private String status;
  private String priority;
  private List<TimeEntryResponseDto> timeEntries; // Изменение на TimeEntryDto

}
