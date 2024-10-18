package ru.baysarov.statistic.service.impl;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import ru.baysarov.statistic.Model.Task;
import ru.baysarov.statistic.Model.TimeEntry;
import ru.baysarov.statistic.Repostiory.TaskRepository;
import ru.baysarov.statistic.Repostiory.TimeEntryRepository;
import ru.baysarov.statistic.dto.TaskStatusStatisticsDto;
import ru.baysarov.statistic.dto.UserDto;
import ru.baysarov.statistic.feign.UserClient;

@ExtendWith(MockitoExtension.class)
class StatisticServiceImplTest {

  @Mock
  private TaskRepository taskRepository;

  @Mock
  private TimeEntryRepository timeEntryRepository;

  @Mock
  private UserClient userClient;

  @InjectMocks
  private StatisticServiceImpl statisticService;

  private UserDto userDto;

  @BeforeEach
  void setUp() {
    userDto = new UserDto();
    userDto.setId(1);
    userDto.setEmail("test@bk.ru");
  }

  @Test
  void testTimeSpentReportAsManager() {
    LocalDate startDate = LocalDate.now().minusDays(5);
    LocalDate endDate = LocalDate.now();

    Task task = mock(Task.class);
    when(task.getId()).thenReturn(1);

    TimeEntry entry1 = new TimeEntry();
    entry1.setDate(LocalDate.now());
    entry1.setHours(5.5F);
    entry1.setUserId(1);
    entry1.setTask(task);

    when(timeEntryRepository.findByDateBetween(any(), any())).thenReturn(Arrays.asList(entry1));

    ByteArrayInputStream report = statisticService.timeSpentReport("manager@bk.ru", startDate, endDate, true);

    assertNotNull(report);
  }


  @Test
  void testTimeSpentReportAsUser() {
    LocalDate startDate = LocalDate.now().minusDays(5);
    LocalDate endDate = LocalDate.now();

    UserDto userDto = new UserDto();
    userDto.setId(1);
    userDto.setEmail("test@bk.ru");

    Task task = mock(Task.class);
    when(task.getId()).thenReturn(1);

    TimeEntry entry1 = new TimeEntry();
    entry1.setDate(LocalDate.now());
    entry1.setHours(3.7F);
    entry1.setUserId(userDto.getId());
    entry1.setTask(task);

    when(userClient.getUserByEmail("test@bk.ru")).thenReturn(ResponseEntity.ok(userDto));

    when(timeEntryRepository.findByUserIdAndDateBetween(eq(userDto.getId()), any(), any()))
        .thenReturn(Arrays.asList(entry1));

    ByteArrayInputStream report = statisticService.timeSpentReport("test@bk.ru", startDate, endDate, false);

    assertNotNull(report);
  }


  @Test
  void testAllTasksStatuses() {
    TaskStatusStatisticsDto dto = new TaskStatusStatisticsDto();

    when(taskRepository.getTaskStatusStatistics()).thenReturn(dto);

    TaskStatusStatisticsDto result = statisticService.allTasksStatuses();

    assertNotNull(result);
  }
}
