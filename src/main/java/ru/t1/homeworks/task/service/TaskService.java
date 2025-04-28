package ru.t1.homeworks.task.service;

import ru.t1.homeworks.task.service.dto.TaskDto;
import ru.t1.homeworks.task.service.dto.TaskRequestDto;

import java.util.List;

public interface TaskService {

    TaskDto getById(Long id);

    Long create(TaskRequestDto dto);

    TaskDto update(Long id, TaskRequestDto dto);

    void delete(Long id);

    List<TaskDto> getTasks();
}
