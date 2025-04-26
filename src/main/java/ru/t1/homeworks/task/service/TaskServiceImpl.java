package ru.t1.homeworks.task.service;


import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.t1.homeworks.task.dao.TaskRepository;
import ru.t1.homeworks.task.entity.Task;
import ru.t1.homeworks.task.exception.TaskNotFoundException;
import ru.t1.homeworks.task.service.dto.TaskRequestDto;
import ru.t1.homeworks.task.service.dto.TaskDto;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final ModelMapper modelMapper;

    @Override
    public Task getById(Long id) {
        return taskRepository.findById(id).orElseThrow(() -> new TaskNotFoundException("Task not found"));
    }

    @Override
    public Long create(TaskRequestDto dto) {
        Task task = modelMapper.map(dto, Task.class);
        Task savedTask = taskRepository.save(task);
        return savedTask.getId();
    }

    @Override
    public Task update(Long id, TaskRequestDto dto) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new TaskNotFoundException("Task not found"));
        task.setId(id);
        task.setDescription(dto.getDescription());
        task.setTitle(dto.getTitle());
        task.setUserId(dto.getUserId());
        return taskRepository.save(task);
    }
}
