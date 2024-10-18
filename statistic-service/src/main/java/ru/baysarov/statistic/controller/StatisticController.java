package ru.baysarov.statistic.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

  @Operation(summary = "Get all task statuses", description = "Retrieve statistics of all task statuses.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successful retrieval of task statuses"),
      @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  @GetMapping("/tasks/statuses")
  public ResponseEntity<?> allTasksStatuses() {
    log.info("Retrieving all task statuses");
    try {
      TaskStatusStatisticsDto taskStatusStatisticsDto = statisticService.allTasksStatuses();
      log.info("Successfully retrieved task statuses");
      return ResponseEntity.ok(taskStatusStatisticsDto);
    } catch (Exception e) {
      log.error("Error retrieving task statuses: {}", e.getMessage(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Ошибка при получении статистики задач: " + e.getMessage());
    }
  }

  @Operation(summary = "Get time spent report for the team", description = "Generate a CSV report of time spent by the team.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successful report generation"),
      @ApiResponse(responseCode = "403", description = "Forbidden if user is not a manager"),
      @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  @GetMapping("/time_spent_report/manager")
  public ResponseEntity<InputStreamResource> getTimeReportTeam(
      @Parameter(description = "Email of the authenticated user", required = true)
      @RequestHeader("X-auth-user-email") String email,
      @RequestParam(required = false) LocalDate startDate,
      @RequestParam(required = false) LocalDate endDate) {

    log.info("Generating time report for team. User: {}", email);
    List<String> roles = userService.getUserRoles(email);
    if (!roles.contains("MANAGER")) {
      log.warn("User {} is not a manager, access denied.", email);
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
    }

    ByteArrayInputStream report = statisticService.timeSpentReport(email, startDate, endDate, true);
    HttpHeaders headers = new HttpHeaders();
    headers.add("Content-Disposition", "attachment; filename=time_report.csv");

    log.info("Successfully generated time report for team");
    return ResponseEntity.ok()
        .headers(headers)
        .body(new InputStreamResource(report));
  }

  @Operation(summary = "Get time spent report for the user", description = "Generate a CSV report of time spent by the user.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successful report generation"),
      @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  @GetMapping("/time_spent_report")
  public ResponseEntity<InputStreamResource> getTimeReportUser(
      @Parameter(description = "Email of the authenticated user", required = true)
      @RequestHeader("X-auth-user-email") String email,
      @RequestParam(required = false) LocalDate startDate,
      @RequestParam(required = false) LocalDate endDate) {

    log.info("Generating time report for user. User: {}", email);
    if (startDate == null) {
      startDate = LocalDate.now().minusMonths(1);
    }
    if (endDate == null) {
      endDate = LocalDate.now();
    }
    ByteArrayInputStream report = statisticService.timeSpentReport(email, startDate, endDate,
        false);
    HttpHeaders headers = new HttpHeaders();
    headers.add("Content-Disposition", "attachment; filename=time_report.csv");

    log.info("Successfully generated time report for user");
    return ResponseEntity.ok()
        .headers(headers)
        .body(new InputStreamResource(report));
  }
}
