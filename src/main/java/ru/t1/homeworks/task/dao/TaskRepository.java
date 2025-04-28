package ru.t1.homeworks.task.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.t1.homeworks.task.entity.Task;


@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

}
