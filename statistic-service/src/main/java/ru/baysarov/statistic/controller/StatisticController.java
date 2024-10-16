package ru.baysarov.statistic.controller;


import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.baysarov.statistic.dto.TaskStatusStatisticsDto;
import ru.baysarov.statistic.service.StatisticService;
import ru.baysarov.statistic.service.UserService;

@RequestMapping("api/v1/statistic")
@RestController
@Slf4j
public class StatisticController {

  private final StatisticService statisticService;
  private final UserService userService;

  public StatisticController(StatisticService statisticService,
      UserService userService) {
    this.statisticService = statisticService;
    this.userService = userService;
  }

  @GetMapping("/tasks/statuses")
  public ResponseEntity<?> allTasksStatuses() {
    try {
      TaskStatusStatisticsDto taskStatusStatisticsDto = statisticService.allTasksStatuses();
      return ResponseEntity.ok(taskStatusStatisticsDto);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Ошибка при получении статистики задач: " + e.getMessage());
    }
  }




  @GetMapping("/time_spent_report/manager")
  public ResponseEntity<InputStreamResource> getTimeReportTeam(
      @RequestHeader("X-auth-user-email") String email,
      @RequestParam(required = false) LocalDate startDate,
      @RequestParam(required = false) LocalDate endDate) {
    List<String> roles = userService.getUserRoles(email);
    if (!roles.contains("MANAGER")) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
    }
    if (startDate == null) {
      startDate = LocalDate.now().minusMonths(1);
    }
    if (endDate == null) {
      endDate = LocalDate.now();
    }

    ByteArrayInputStream report = statisticService.timeSpentReport(email, startDate, endDate, true);
    HttpHeaders headers = new HttpHeaders();
    headers.add("Content-Disposition", "attachment; filename=time_report.csv");

    return ResponseEntity.ok()
        .headers(headers)
        .body(new InputStreamResource(report));
  }

  @GetMapping("/time_spent_report")
  public ResponseEntity<InputStreamResource> getTimeReportUser(@RequestHeader("X-auth-user-email") String email,
      @RequestParam(required = false) LocalDate startDate,
      @RequestParam(required = false) LocalDate endDate) {

    if (startDate == null) {
      startDate = LocalDate.now().minusMonths(1);
    }
    if (endDate == null) {
      endDate = LocalDate.now();
    }
    ByteArrayInputStream report = statisticService.timeSpentReport(email, startDate, endDate, false);
    HttpHeaders headers = new HttpHeaders();
    headers.add("Content-Disposition", "attachment; filename=time_report.csv");

    return ResponseEntity.ok()
        .headers(headers)
        .body(new InputStreamResource(report));

  }
}
