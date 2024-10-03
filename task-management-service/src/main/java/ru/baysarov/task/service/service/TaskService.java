package ru.baysarov.task.service.service;

import java.time.LocalDateTime;
import java.util.List;
import ru.baysarov.task.service.dto.TaskDto;

public interface TaskService {
  void createTask(TaskDto taskDto, int authorId);
  TaskDto getTaskById(int id);
  List<TaskDto> getAllTasks();
  void updateTask(int id, TaskDto updatedTask);
  void assignTask(int taskId, int authorId);
  void deleteTask(int id);
  TaskDto setTaskDeadline(int taskId, LocalDateTime deadLine, int userId);
}
