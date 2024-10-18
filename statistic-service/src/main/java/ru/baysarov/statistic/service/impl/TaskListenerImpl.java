package ru.baysarov.statistic.service.impl;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.baysarov.statistic.dto.TaskDto;
import ru.baysarov.statistic.service.TaskListener;
import ru.baysarov.statistic.service.TaskService;

/**
 * Реализация слушателя для обработки событий, связанных с задачами, получаемых из Kafka.
 */
@Service
public class TaskListenerImpl implements TaskListener {

  private final TaskService taskService;


  public TaskListenerImpl(TaskService taskService) {
    this.taskService = taskService;
  }

  /**
   * Обрабатывает событие создания задачи.
   *
   * @param taskDto объект задачи, который был создан
   */
  @KafkaListener(topics = "task_created", groupId = "task_group", containerFactory = "taskDtoListenerFactory")
  @Override
  public void onTaskCreated(TaskDto taskDto) {
    System.out.println("Received task created: " + taskDto);
    taskService.saveTask(taskDto);
  }

  /**
   * Обрабатывает событие обновления задачи.
   *
   * @param taskDto объект задачи, который был обновлен
   */
  @KafkaListener(topics = "task_updated", groupId = "task_group", containerFactory = "taskDtoListenerFactory")
  @Override
  public void onTaskUpdated(TaskDto taskDto) {
    System.out.println("Received task updated: " + taskDto);
    taskService.updateTask(taskDto);
  }

  /**
   * Обрабатывает событие удаления задачи.
   *
   * @param taskDto объект задачи, который был удален
   */
  @KafkaListener(topics = "task_deleted", groupId = "task_group", containerFactory = "taskDtoListenerFactory")
  @Override
  public void onTaskDeleted(TaskDto taskDto) {
    System.out.println("Received task deleted: " + taskDto);
    taskService.deleteTask(taskDto);
  }
}
