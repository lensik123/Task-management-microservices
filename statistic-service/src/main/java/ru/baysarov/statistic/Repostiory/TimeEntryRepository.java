package ru.baysarov.statistic.Repostiory;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.baysarov.statistic.Model.TimeEntry;

@Repository
public interface TimeEntryRepository extends JpaRepository<TimeEntry, Integer> {

  List<TimeEntry> findByUserIdAndDateBetween(Integer userId, LocalDate startDate,
      LocalDate endDate);

  List<TimeEntry> findByTaskIdAndDateBetween(Integer taskId, LocalDate startDate,
      LocalDate endDate);

  List<TimeEntry> findByDateBetween(LocalDate startDate, LocalDate endDate);
}

