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
import ru.baysarov.task.service.dto.TimeEntryDtoIn;
import ru.baysarov.task.service.service.TaskService;
import ru.baysarov.task.service.service.TimeEntryService;

/**
 * Контроллер для управления задачами.
 */
@RestController
@RequestMapping("api/v1/tasks")
@Slf4j
public class TasksController {

  private final TaskService taskService;
  private final TimeEntryService timeEntryService;

  public TasksController(TaskService taskService, TimeEntryService timeEntryService) {
    this.taskService = taskService;
    this.timeEntryService = timeEntryService;
  }

  /**
   * Получает задачу по идентификатору.
   *
   * @param id идентификатор задачи
   * @return задача с указанным идентификатором
   */
  @GetMapping("/{id}")
  public TaskDtoOut getTask(@PathVariable int id) {
    log.info("Fetching task with id: {}", id);
    TaskDtoOut task = taskService.getTaskById(id);
    log.info("Task with id: {} retrieved successfully", id);
    return task;
  }

  /**
   * Получает все задачи с учетом пагинации.
   *
   * @param page номер страницы
   * @param size размер страницы
   * @return все задачи на указанной странице
   */
  @GetMapping()
  public AllTasksResponse getAllTasks(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size
  ) {
    log.info("Fetching all tasks, page: {}, size: {}", page, size);
    AllTasksResponse response = new AllTasksResponse(taskService.getAllTasks(page, size));
    log.info("All tasks retrieved successfully");
    return response;
  }

  /**
   * Создает новую задачу.
   *
   * @param taskDtoIn данные задачи
   * @param bindingResult результаты валидации
   * @param email email пользователя, создающего задачу
   * @return ответ с результатом создания задачи
   */
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

  /**
   * Назначает задачу пользователю.
   *
   * @param id идентификатор задачи
   * @param request данные для назначения задачи
   * @return ответ с результатом назначения
   */
  @PatchMapping("/{id}/assignee")
  public ResponseEntity<?> assignTask(@PathVariable int id, @RequestBody AssignTaskRequest request) {
    log.info("Assigning task with id: {} to user: {}", id, request.getAssigneeEmail());
    taskService.assignTask(id, request.getAssigneeEmail());
    log.info("Task with id: {} assigned to user: {}", id, request.getAssigneeEmail());
    return ResponseEntity.ok(HttpStatus.OK);
  }

  /**
   * Обновляет задачу.
   *
   * @param id идентификатор задачи
   * @param updatedTask обновленные данные задачи
   * @param bindingResult результаты валидации
   * @return ответ с результатом обновления
   */
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

  /**
   * Регистрирует затраченное время на задачу.
   *
   * @param taskId идентификатор задачи
   * @param timeEntryDtoIn данные о затраченном времени
   * @param userEmail email пользователя, который регистрирует время
   * @return ответ с результатом регистрации времени
   */
  @PostMapping("/time_spent/{taskId}")
  public ResponseEntity<?> timeSpent(@PathVariable int taskId, @RequestBody @Valid TimeEntryDtoIn timeEntryDtoIn,
      @RequestHeader("X-auth-user-email") String userEmail) {
    timeEntryService.saveTimeEntry(taskId,userEmail, timeEntryDtoIn);
    return ResponseEntity.ok(HttpStatus.OK);
  }

  /**
   * Удаляет задачу по идентификатору.
   *
   * @param id идентификатор задачи
   * @return ответ с результатом удаления
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteTaskById(@PathVariable int id) {
    log.info("Deleting task with id: {}", id);
    taskService.deleteTask(id);
    log.info("Task with id: {} deleted successfully", id);
    return ResponseEntity.ok(HttpStatus.OK);
  }

  /**
   * Устанавливает срок выполнения для задачи.
   *
   * @param id идентификатор задачи
   * @param request данные для установки срока
   * @param bindingResult результаты валидации
   * @param userEmail email пользователя, устанавливающего срок
   * @return ответ с результатом установки срока
   */
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

  /**
   * Обрабатывает ошибки валидации и возвращает ответ с сообщениями об ошибках.
   *
   * @param bindingResult результаты валидации
   * @return ответ с ошибками валидации, если они есть
   */
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
