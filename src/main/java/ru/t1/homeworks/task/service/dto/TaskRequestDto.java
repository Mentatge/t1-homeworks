package ru.t1.homeworks.task.service.dto;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskRequestDto {

    @Size(max = 50)
    private String title;
    @Size(max = 100)
    private String description;
    @NotNull
    private Long userId;
}
