package ru.t1.homeworks.task.service.dto;


import lombok.Getter;
import lombok.Setter;
import ru.t1.homeworks.task.entity.TaskStatus;

@Getter
@Setter
public class TaskDto {
    private Long id;
    private String title;
    private String description;
    private Long userId;
    private TaskStatus status;
}
