package ru.baysarov.task.service.dto;


import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TimeEntryDtoIn {

  @NotNull
  private LocalDate date;

  @NotNull
  private Float hours;


}