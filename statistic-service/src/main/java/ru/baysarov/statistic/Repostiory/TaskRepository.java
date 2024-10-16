package ru.baysarov.statistic.Repostiory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.baysarov.statistic.Model.Task;
import ru.baysarov.statistic.dto.TaskStatusStatisticsDto;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {


  @Query("SELECT new ru.baysarov.statistic.dto.TaskStatusStatisticsDto( "
      + "    COUNT(t), "
      + "    SUM(CASE WHEN t.status = 'WAITING' THEN 1 ELSE 0 END), "
      + "    SUM(CASE WHEN t.status = 'IN_PROCESS' THEN 1 ELSE 0 END), "
      + "    SUM(CASE WHEN t.status = 'DONE' THEN 1 ELSE 0 END) "
      + ") FROM Task t")
  TaskStatusStatisticsDto getTaskStatusStatistics();


}
