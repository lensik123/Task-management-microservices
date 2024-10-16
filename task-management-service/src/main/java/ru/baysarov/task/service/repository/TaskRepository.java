package ru.baysarov.task.service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.baysarov.task.service.model.Task;


@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {
  Page<Task> findAll(Pageable pageable);

}
