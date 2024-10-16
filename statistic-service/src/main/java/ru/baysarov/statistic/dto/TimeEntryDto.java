package ru.baysarov.statistic.dto;

import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TimeEntryDto {

  private Integer id;
  private Integer taskId;
  private Integer userId;
  private LocalDate date;
  private Float hours;
}
