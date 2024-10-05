package ru.baysarov.task.service.controller;


import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.springframework.web.bind.annotation.RestController;
import ru.baysarov.task.service.dto.SetDeadlineRequest;
import ru.baysarov.task.service.dto.TaskDto;
import ru.baysarov.task.service.dto.AssignTaskRequest;
import ru.baysarov.task.service.service.TaskService;
import ru.baysarov.task.service.service.TaskServiceImpl;

@RestController
@RequestMapping("api/v1/tasks")
public class TasksController {

  private final TaskService taskService;

  public TasksController(TaskServiceImpl taskServiceImpl) {
    this.taskService = taskServiceImpl;
  }


  @GetMapping("/{id}")
  public TaskDto getTask(@PathVariable int id) {
    return taskService.getTaskById(id);
  }

  @GetMapping()
  public List<TaskDto> getAllTasks() {
    return taskService.getAllTasks();
  }

  @PostMapping()
  public ResponseEntity<?> createTask(@RequestBody @Valid TaskDto taskDto, BindingResult bindingResult,
      @RequestHeader("X-auth-user-email") String email) {
    ResponseEntity<?> errors = getResponseEntity(bindingResult);
    if (errors != null) {
      return errors;
    }
    taskService.createTask(taskDto, email);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @PatchMapping("/{id}/assignee")
  public ResponseEntity<?> assignTask(@PathVariable int id,
      @RequestBody AssignTaskRequest request) {
    taskService.assignTask(id, request.getAssigneeEmail());
    return ResponseEntity.ok(HttpStatus.OK);
  }

  @PutMapping("/{id}")
  public ResponseEntity<?> updateTask(@PathVariable int id,
      @RequestBody @Valid TaskDto updatedTask, BindingResult bindingResult) {
    ResponseEntity<?> errors = getResponseEntity(bindingResult);
    if (errors != null) {
      return errors;
    }
    taskService.updateTask(id, updatedTask);
    return ResponseEntity.ok(HttpStatus.OK);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteTaskById(@PathVariable int id) {
    taskService.deleteTask(id);
    return ResponseEntity.ok(HttpStatus.OK);
  }

  @PatchMapping("/{id}/deadline")
  public ResponseEntity<?> setTaskDeadline(@PathVariable int id,
      @RequestBody @Valid SetDeadlineRequest request,
      @RequestHeader("X-auth-user-email") String userEmail) {

    taskService.setTaskDeadline(id, request.getDeadline(), userEmail);
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
