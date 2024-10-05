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

  /**
   * Получает задачу по указанному идентификатору.
   *
   * @param id идентификатор задачи
   * @return объект TaskDto, представляющий запрашиваемую задачу
   */
  @GetMapping("/{id}")
  public TaskDto getTask(@PathVariable int id) {
    return taskService.getTaskById(id);
  }

  /**
   * Получает список всех задач.
   *
   * @return список объектов TaskDto, представляющих все задачи
   */
  @GetMapping()
  public List<TaskDto> getAllTasks() {
    return taskService.getAllTasks();
  }

  /**
   * Создает новую задачу.
   *
   * @param taskDto объект TaskDto, содержащий данные создаваемой задачи
   * @param bindingResult объект BindingResult, содержащий результаты валидации
   * @param email адрес электронной почты пользователя, создающего задачу
   * @return ResponseEntity с кодом состояния 201 (CREATED) в случае успешного создания
   *         или объект ResponseEntity с сообщением об ошибках, если валидация не прошла
   */
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

  /**
   * Назначает исполнителя для указанной задачи.
   *
   * @param id идентификатор задачи
   * @param request объект AssignTaskRequest, содержащий данные о назначении исполнителя
   * @return ResponseEntity с кодом состояния 200 (OK)
   */
  @PatchMapping("/{id}/assignee")
  public ResponseEntity<?> assignTask(@PathVariable int id,
      @RequestBody AssignTaskRequest request) {
    taskService.assignTask(id, request.getAssigneeEmail());
    return ResponseEntity.ok(HttpStatus.OK);
  }

  /**
   * Обновляет данные указанной задачи.
   *
   * @param id идентификатор задачи
   * @param updatedTask объект TaskDto, содержащий обновленные данные задачи
   * @param bindingResult объект BindingResult, содержащий результаты валидации
   * @return ResponseEntity с кодом состояния 200 (OK) в случае успешного обновления
   *         или объект ResponseEntity с сообщением об ошибках, если валидация не прошла
   */
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

  /**
   * Удаляет задачу по указанному идентификатору.
   *
   * @param id идентификатор задачи
   * @return ResponseEntity с кодом состояния 200 (OK)
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteTaskById(@PathVariable int id) {
    taskService.deleteTask(id);
    return ResponseEntity.ok(HttpStatus.OK);
  }

  /**
   * Устанавливает срок выполнения для указанной задачи.
   *
   * @param id идентификатор задачи
   * @param request объект SetDeadlineRequest, содержащий новую дату выполнения
   * @param bindingResult объект BindingResult, содержащий результаты валидации
   * @param userEmail адрес электронной почты пользователя, устанавливающего срок выполнения
   * @return ResponseEntity с кодом состояния 200 (OK) в случае успешного обновления
   *         или объект ResponseEntity с сообщением об ошибках, если валидация не прошла
   */
  @PatchMapping("/{id}/deadline")
  public ResponseEntity<?> setTaskDeadline(@PathVariable int id,
      @RequestBody @Valid SetDeadlineRequest request, BindingResult bindingResult,
      @RequestHeader("X-auth-user-email") String userEmail) {
    ResponseEntity<?> errors = getResponseEntity(bindingResult);
    if (errors != null) {
      return errors;
    }
    taskService.setTaskDeadline(id, request.getDeadline(), userEmail);
    return ResponseEntity.ok(HttpStatus.OK);
  }

  /**
   * Проверяет наличие ошибок валидации и возвращает соответствующий
   * объект ResponseEntity с сообщением об ошибках, если таковые имеются.
   *
   * @param bindingResult объект BindingResult, содержащий результаты валидации
   * @return ResponseEntity с кодом состояния 400 (BAD_REQUEST) и телом,
   *         содержащим поля и сообщения об ошибках, если ошибки присутствуют;
   *         null, если ошибок нет
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
