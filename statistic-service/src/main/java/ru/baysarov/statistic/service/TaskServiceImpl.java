package ru.baysarov.statistic.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.baysarov.statistic.Model.Task;
import ru.baysarov.statistic.Repostiory.TaskRepository;
import ru.baysarov.statistic.dto.TaskDto;
import ru.baysarov.statistic.mapper.TaskMapper;


@Service
@Transactional
public class TaskServiceImpl implements TaskService{

  private final TaskMapper taskMapper;
  private final TaskRepository taskRepository;

  public TaskServiceImpl(TaskMapper taskMapper, TaskRepository taskRepository) {
    this.taskMapper = taskMapper;
    this.taskRepository = taskRepository;
  }

  @Override
  public void saveTask(TaskDto TaskDto) {
    Task task = taskMapper.toEntity(TaskDto);
    taskRepository.save(task);
  }

  @Override
  public void deleteTask(TaskDto taskDto) {
    Task task = taskMapper.toEntity(taskDto);
    taskRepository.delete(task);
  }

  @Override
  public void updateTask(TaskDto taskDto) {
    Task task = taskMapper.toEntity(taskDto);
    taskRepository.save(task);
  }
}
