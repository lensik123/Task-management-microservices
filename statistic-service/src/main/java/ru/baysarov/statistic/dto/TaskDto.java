package ru.baysarov.statistic.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TaskDto {

  private Integer id;
  private String title;
  private String description;
  private Integer assigneeId;
  private Integer authorId;

  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  private LocalDateTime deadline;

  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  private LocalDateTime createdAt;

  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  private LocalDateTime updatedAt;

  private String priority;
  private String status;

  private Double timeSpentHours;

}
