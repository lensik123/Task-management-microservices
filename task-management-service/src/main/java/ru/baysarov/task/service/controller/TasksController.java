package ru.baysarov.task.service.controller;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.baysarov.task.service.dto.AllTasksResponse;
import ru.baysarov.task.service.dto.SetDeadlineRequest;
import ru.baysarov.task.service.dto.TaskDtoIn;
import ru.baysarov.task.service.dto.AssignTaskRequest;
import ru.baysarov.task.service.dto.TaskDtoOut;
import ru.baysarov.task.service.service.TaskService;

@RestController
@RequestMapping("api/v1/tasks")
@Slf4j
public class TasksController {

  private final TaskService taskService;

  public TasksController(TaskService taskService) {
    this.taskService = taskService;
  }

  @GetMapping("/{id}")
  public TaskDtoOut getTask(@PathVariable int id) {
    log.info("Fetching task with id: {}", id);
    TaskDtoOut task = taskService.getTaskById(id);
    log.info("Task with id: {} retrieved successfully", id);
    return task;
  }


  //TODO: корректность логов проверить. СДелать stream
  //TODO: сделать вывод только всех своих тасков
  @GetMapping()
  public AllTasksResponse getAllTasks(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(defaultValue = "false") boolean isMyTasks
  ) {
    log.info("Fetching all tasks, page: {}, size: {}, isMyTasks: {}", page, size, isMyTasks);
    AllTasksResponse response = new AllTasksResponse(taskService.getAllTasks(page, size, isMyTasks));
    log.info("All tasks retrieved successfully");
    return response;
  }

  @PostMapping()
  public ResponseEntity<?> createTask(@RequestBody @Valid TaskDtoIn taskDtoIn, BindingResult bindingResult,
      @RequestHeader("X-auth-user-email") String email) {
    log.info("Creating new task by user: {}", email);
    ResponseEntity<?> errors = getResponseEntity(bindingResult);
    if (errors != null) {
      log.error("Validation errors during task creation: {}", errors.getBody());
      return errors;
    }
    taskService.createTask(taskDtoIn, email);
    log.info("Task created successfully by user: {}", email);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @PatchMapping("/{id}/assignee")
  public ResponseEntity<?> assignTask(@PathVariable int id, @RequestBody AssignTaskRequest request) {
    log.info("Assigning task with id: {} to user: {}", id, request.getAssigneeEmail());
    taskService.assignTask(id, request.getAssigneeEmail());
    log.info("Task with id: {} assigned to user: {}", id, request.getAssigneeEmail());
    return ResponseEntity.ok(HttpStatus.OK);
  }

  @PutMapping("/{id}")
  public ResponseEntity<?> updateTask(@PathVariable int id, @RequestBody @Valid TaskDtoIn updatedTask,
      BindingResult bindingResult) {
    log.info("Updating task with id: {}", id);
    ResponseEntity<?> errors = getResponseEntity(bindingResult);
    if (errors != null) {
      log.error("Validation errors during task update: {}", errors.getBody());
      return errors;
    }
    taskService.updateTask(id, updatedTask);
    log.info("Task with id: {} updated successfully", id);
    return ResponseEntity.ok(HttpStatus.OK);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteTaskById(@PathVariable int id) {
    log.info("Deleting task with id: {}", id);
    taskService.deleteTask(id);
    log.info("Task with id: {} deleted successfully", id);
    return ResponseEntity.ok(HttpStatus.OK);
  }

  @PatchMapping("/{id}/deadline")
  public ResponseEntity<?> setTaskDeadline(@PathVariable int id, @RequestBody @Valid SetDeadlineRequest request,
      BindingResult bindingResult, @RequestHeader("X-auth-user-email") String userEmail) {
    log.info("Setting deadline for task with id: {} by user: {}", id, userEmail);
    ResponseEntity<?> errors = getResponseEntity(bindingResult);
    if (errors != null) {
      log.error("Validation errors during setting deadline: {}", errors.getBody());
      return errors;
    }
    taskService.setTaskDeadline(id, request.getDeadline(), userEmail);
    log.info("Deadline set for task with id: {} by user: {}", id, userEmail);
    return ResponseEntity.ok(HttpStatus.OK);
  }


  static ResponseEntity<?> getResponseEntity(BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      Map<String, String> errors = new HashMap<>();
      for (FieldError error : bindingResult.getFieldErrors()) {
        errors.put(error.getField(), error.getDefaultMessage());
      }
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }
    return null;
  }
}
