package ru.t1.homeworks.task.service;


import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.t1.homeworks.aspect.annotation.LogAfterReturning;
import ru.t1.homeworks.aspect.annotation.LogAfterThrowing;
import ru.t1.homeworks.aspect.annotation.LogAround;
import ru.t1.homeworks.aspect.annotation.LogBefore;
import ru.t1.homeworks.config.kafka.KafkaConfig;
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
    private final KafkaTemplate<Long, String> kafkaTemplate;


    @Override
    @LogBefore
    public TaskDto getById(Long id) {
        Task task = findById(id);
        return modelMapper.map(task, TaskDto.class);
    }

    @Override
    @LogAfterReturning
    public Long create(TaskRequestDto dto) {
        Task task = modelMapper.map(dto, Task.class);
        Task savedTask = taskRepository.save(task);
        return savedTask.getId();
    }

    @Override
    @LogAfterThrowing
    @LogAround
    public TaskDto update(Long id, TaskRequestDto dto) {
        String newStatus = dto.getStatus();
        Task task = findById(id);
        task.setDescription(dto.getDescription());
        task.setTitle(dto.getTitle());
        task.setUserId(dto.getUserId());
        String oldStatus = task.getStatus();
        task.setStatus(newStatus);
        Task saved = taskRepository.save(task);
        if (!oldStatus.equals(newStatus)) {
            kafkaTemplate.send(
                    KafkaConfig.TOPIC,
                    saved.getId(),
                    newStatus
            );
        }
        return modelMapper.map(task, TaskDto.class);
    }

    @Override
    @LogAround
    public void delete(Long id) {
        Task task = findById(id);
        taskRepository.delete(task);
    }

    @Override
    @LogBefore
    @LogAfterReturning
    @LogAround
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
