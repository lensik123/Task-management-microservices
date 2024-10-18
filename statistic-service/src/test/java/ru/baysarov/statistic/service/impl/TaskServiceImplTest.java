package ru.baysarov.statistic.service.impl;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.baysarov.statistic.Model.Task;
import ru.baysarov.statistic.Repostiory.TaskRepository;
import ru.baysarov.statistic.dto.TaskDto;
import ru.baysarov.statistic.mapper.TaskMapper;

public class TaskServiceImplTest {

  @InjectMocks
  private TaskServiceImpl taskService;

  @Mock
  private TaskMapper taskMapper;

  @Mock
  private TaskRepository taskRepository;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testSaveTask() {
    TaskDto taskDto = new TaskDto();
    Task task = new Task();

    when(taskMapper.toEntity(taskDto)).thenReturn(task);

    taskService.saveTask(taskDto);

    verify(taskMapper, times(1)).toEntity(taskDto);
    verify(taskRepository, times(1)).save(task);
  }

  @Test
  void testDeleteTask() {
    TaskDto taskDto = new TaskDto();
    Task task = new Task();

    when(taskMapper.toEntity(taskDto)).thenReturn(task);

    taskService.deleteTask(taskDto);

    verify(taskMapper, times(1)).toEntity(taskDto);
    verify(taskRepository, times(1)).delete(task);
  }

  @Test
  void testUpdateTask() {
    TaskDto taskDto = new TaskDto();
    Task task = new Task();

    when(taskMapper.toEntity(taskDto)).thenReturn(task);

    taskService.updateTask(taskDto);

    verify(taskMapper, times(1)).toEntity(taskDto);
    verify(taskRepository, times(1)).save(task);
  }
}
