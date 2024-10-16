package ru.baysarov.statistic.service;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import ru.baysarov.statistic.dto.TaskStatusStatisticsDto;

public interface StatisticService {
  ByteArrayInputStream timeSpentReport(String userEmail, LocalDate startDate, LocalDate endDate, boolean isManager);
  TaskStatusStatisticsDto allTasksStatuses();
}
