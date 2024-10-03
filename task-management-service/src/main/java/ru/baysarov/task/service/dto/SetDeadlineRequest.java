package ru.baysarov.task.service.dto;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SetDeadlineRequest {

  private LocalDateTime deadline;

}
