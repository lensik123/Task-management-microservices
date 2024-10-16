package ru.baysarov.statistic.service;

import org.springframework.stereotype.Service;
import ru.baysarov.statistic.dto.TaskDto;

@Service
public interface TaskService {

  void saveTask(TaskDto taskDto);

  void deleteTask(TaskDto taskDto);
  void updateTask(TaskDto taskDto);
}
