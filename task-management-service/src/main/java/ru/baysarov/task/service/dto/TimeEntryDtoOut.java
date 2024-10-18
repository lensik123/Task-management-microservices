package ru.baysarov.task.service.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimeEntryDtoOut {

  private Integer id;
  private Integer taskId;
  private Integer userId;
  private LocalDate date;
  private Float hours;
}
