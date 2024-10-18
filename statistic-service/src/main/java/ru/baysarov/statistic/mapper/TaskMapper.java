package ru.baysarov.statistic.mapper;

import org.mapstruct.Mapper;
import ru.baysarov.statistic.Model.Task;
import ru.baysarov.statistic.dto.TaskDto;

@Mapper(componentModel = "spring")
public interface TaskMapper {

  Task toEntity(TaskDto taskDto);

  TaskDto toDto(Task task);
}
