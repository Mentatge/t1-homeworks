package ru.t1.homeworks.task.service;


import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.t1.homeworks.task.dao.TaskRepository;
import ru.t1.homeworks.task.entity.Task;
import ru.t1.homeworks.task.entity.TaskStatus;
import ru.t1.homeworks.task.exception.TaskNotFoundException;
import ru.t1.homeworks.task.service.dto.TaskRequestDto;
import ru.t1.homeworks.task.service.dto.TaskDto;
import ru.t1.logging.aspect.annotation.LogAfterReturning;
import ru.t1.logging.aspect.annotation.LogAfterThrowing;
import ru.t1.logging.aspect.annotation.LogAround;
import ru.t1.logging.aspect.annotation.LogBefore;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final ModelMapper modelMapper;
    private final KafkaTemplate<Long, String> kafkaTemplate;

    @Value("${kafka.topics.task-status}")
    private String taskStatusTopic;


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
        TaskStatus newStatus = dto.getStatus();
        Task task = findById(id);
        task.setDescription(dto.getDescription());
        task.setTitle(dto.getTitle());
        task.setUserId(dto.getUserId());
        String oldStatus = task.getStatus().name();
        task.setStatus(newStatus);
        Task saved = taskRepository.save(task);
        if (!oldStatus.equals(newStatus.name())) {
            kafkaTemplate.send(
                    taskStatusTopic,
                    saved.getId(),
                    newStatus.name()
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
