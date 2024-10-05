package ru.baysarov.task.service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssignTaskRequest {
  private String assigneeEmail;
}

