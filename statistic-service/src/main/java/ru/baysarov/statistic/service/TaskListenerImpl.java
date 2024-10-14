package ru.baysarov.statistic.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.baysarov.statistic.dto.TaskDto;

@Service
public class TaskListenerImpl implements TaskListener {

  private final TaskService taskService;

  public TaskListenerImpl(TaskService taskService) {
    this.taskService = taskService;
  }
  @KafkaListener(topics = "${spring.kafka.topic.task-created}", groupId = "task_group")
  @Override
  public void onTaskCreated(TaskDto TaskDto) {
    System.out.println("Received task created: " + TaskDto);
    taskService.saveTask(TaskDto);
  }

  @KafkaListener(topics = "${spring.kafka.topic.task-updated}", groupId = "task_group")
  @Override
  public void onTaskUpdated(TaskDto TaskDto) {
    System.out.println("Received task updated: " + TaskDto);
    taskService.saveTask(TaskDto);
  }

  @KafkaListener(topics = "${spring.kafka.topic.task-deleted}", groupId = "task_group")
  @Override
  public void onTaskDeleted(TaskDto TaskDto) {
    System.out.println("Received task deleted: " + TaskDto);
    taskService.deleteTask(TaskDto);
  }
}


