package ru.baysarov.statistic.mapper;

import org.mapstruct.Mapper;
import ru.baysarov.statistic.Model.Task;
import ru.baysarov.statistic.dto.TaskDto;

@Mapper(componentModel = "spring")
public interface TaskMapper {

  // Пример простого маппинга DTO -> Entity
  Task toEntity(TaskDto taskDto);

  // Пример обратного маппинга Entity -> DTO
  TaskDto toDto(Task task);
}
