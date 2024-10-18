package ru.baysarov.statistic.service;

import ru.baysarov.statistic.dto.TaskDto;

public interface TaskListener {

  void onTaskCreated(TaskDto TaskDto);

  void onTaskUpdated(TaskDto TaskDto);

  void onTaskDeleted(TaskDto TaskDto);
}
