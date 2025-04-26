package ru.t1.homeworks.task.service;

import ru.t1.homeworks.task.entity.Task;
import ru.t1.homeworks.task.service.dto.TaskRequestDto;

public interface TaskService {

    Task getById(Long id);

    Long create(TaskRequestDto dto);

    Task update(Long id, TaskRequestDto dto);

    void delete(Long id);
}
