package ru.baysarov.statistic.Repostiory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.baysarov.statistic.Model.Task;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {

}
