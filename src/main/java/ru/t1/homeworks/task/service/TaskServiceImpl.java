package ru.t1.homeworks.task.service;


import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.t1.homeworks.task.dao.TaskRepository;
import ru.t1.homeworks.task.entity.Task;
import ru.t1.homeworks.task.exception.TaskNotFoundException;
import ru.t1.homeworks.task.service.dto.TaskRequestDto;
import ru.t1.homeworks.task.service.dto.TaskDto;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final ModelMapper modelMapper;

    @Override
    public TaskDto getById(Long id) {
        Task task = findById(id);
        return modelMapper.map(task, TaskDto.class);
    }

    @Override
    public Long create(TaskRequestDto dto) {
        Task task = modelMapper.map(dto, Task.class);
        Task savedTask = taskRepository.save(task);
        return savedTask.getId();
    }

    @Override
    public TaskDto update(Long id, TaskRequestDto dto) {
        Task task = findById(id);
        task.setId(id);
        task.setDescription(dto.getDescription());
        task.setTitle(dto.getTitle());
        task.setUserId(dto.getUserId());
        return modelMapper.map(task, TaskDto.class);
    }

    @Override
    public void delete(Long id) {
        Task task = findById(id);
        taskRepository.delete(task);
    }

    @Override
    public List<TaskDto> getTasks() {
        List<Task> tasks = taskRepository.findAll();
        return tasks.stream()
                .map(task -> modelMapper.map(task, TaskDto.class))
                .toList();
    }

    private Task findById(Long id) {
        return taskRepository.findById(id).orElseThrow(() -> new TaskNotFoundException("Task not found"));
    }
}
