package ru.baysarov.task.service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.baysarov.task.service.model.Task;

/**
 * Репозиторий для работы с сущностями задач.
 *
 * Этот интерфейс расширяет JpaRepository, предоставляя методы для выполнения
 * стандартных операций CRUD (создание, чтение, обновление, удаление)
 * над сущностями Task.
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {
  Page<Task> findAll(Pageable pageable);

}
