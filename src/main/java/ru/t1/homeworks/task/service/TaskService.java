package ru.t1.homeworks.task.service;

import ru.t1.homeworks.task.service.dto.TaskDto;

public interface TaskService {

    TaskDto getById(Long id);
}
