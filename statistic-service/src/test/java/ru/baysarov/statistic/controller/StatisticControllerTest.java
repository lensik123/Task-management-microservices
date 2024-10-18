package ru.baysarov.statistic.controller;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.baysarov.statistic.dto.TaskStatusStatisticsDto;
import ru.baysarov.statistic.service.StatisticService;
import ru.baysarov.statistic.service.UserService;

@ExtendWith(MockitoExtension.class)
class StatisticControllerTest {

  @Mock
  private StatisticService statisticService;

  @Mock
  private UserService userService;

  @InjectMocks
  private StatisticController statisticController;

  private MockMvc mockMvc;
  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(statisticController).build();
    objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
  }

  @Test
  void allTasksStatuses_ShouldReturnStatusStatistics() throws Exception {
    TaskStatusStatisticsDto statisticsDto = new TaskStatusStatisticsDto();
    statisticsDto.setDoneTasks(5);
    statisticsDto.setInProcessTasks(3);
    statisticsDto.setWaitingTasks(2);

    when(statisticService.allTasksStatuses()).thenReturn(statisticsDto);

    mockMvc.perform(get("/api/v1/statistic/tasks/statuses"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.doneTasks", is(5)))
        .andExpect(jsonPath("$.inProcessTasks", is(3)))
        .andExpect(jsonPath("$.waitingTasks", is(2)));

    verify(statisticService, times(1)).allTasksStatuses();
  }

  @Test
  void getTimeReportTeam_ShouldReturnReport_WhenUserIsManager() throws Exception {
    String email = "manager@test.com";
    ByteArrayInputStream report = new ByteArrayInputStream("Test report content".getBytes());

    when(userService.getUserRoles(email)).thenReturn(Collections.singletonList("MANAGER"));
    when(statisticService.timeSpentReport(eq(email), eq(null), eq(null), eq(true)))
        .thenReturn(report);

    mockMvc.perform(get("/api/v1/statistic/time_spent_report/manager")
            .header("X-auth-user-email", email))
        .andExpect(status().isOk())
        .andExpect(result -> {
          String contentDisposition = result.getResponse()
              .getHeader(HttpHeaders.CONTENT_DISPOSITION);
          assertThat(contentDisposition, containsString("attachment; filename=time_report.csv"));
        });

    verify(statisticService, times(1)).timeSpentReport(eq(email), eq(null), eq(null), eq(true));
  }

  @Test
  void getTimeReportTeam_ShouldReturnForbidden_WhenUserIsNotManager() throws Exception {
    String email = "employee@test.com";
    when(userService.getUserRoles(email)).thenReturn(Collections.singletonList("EMPLOYEE"));

    mockMvc.perform(get("/api/v1/statistic/time_spent_report/manager")
            .header("X-auth-user-email", email))
        .andExpect(status().isForbidden());

    verify(statisticService, times(0)).timeSpentReport(eq(email), eq(null), eq(null), eq(true));
  }

  @Test
  void getTimeReportUser_ShouldReturnReport() throws Exception {
    String email = "user@test.com";
    ByteArrayInputStream report = new ByteArrayInputStream("Test user report content".getBytes());

    when(statisticService.timeSpentReport(eq(email), eq(LocalDate.now().minusMonths(1)),
        eq(LocalDate.now()), eq(false)))
        .thenReturn(report);

    mockMvc.perform(get("/api/v1/statistic/time_spent_report")
            .header("X-auth-user-email", email))
        .andExpect(status().isOk())
        .andExpect(result -> {
          String contentDisposition = result.getResponse()
              .getHeader(HttpHeaders.CONTENT_DISPOSITION);
          assertThat(contentDisposition, containsString("attachment; filename=time_report.csv"));
        });

    verify(statisticService, times(1)).timeSpentReport(eq(email),
        eq(LocalDate.now().minusMonths(1)), eq(LocalDate.now()), eq(false));
  }
}
