package ru.t1.homeworks.task.web;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.t1.homeworks.task.entity.Task;
import ru.t1.homeworks.task.service.TaskService;
import ru.t1.homeworks.task.service.dto.TaskRequestDto;
import ru.t1.homeworks.task.service.dto.TaskDto;

import java.net.URI;

@RestController
@RequestMapping("api/v1/tasks")
@RequiredArgsConstructor
@Validated
public class TaskController {

    private final TaskService taskService;

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTask(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.getById(id));
    }

    @PostMapping
    public ResponseEntity<Long> createTask(@Valid @RequestBody TaskRequestDto dto) {
        Long createdId = taskService.create(dto);
        URI location = URI.create("/api/v1/tasks/" + createdId);
        return ResponseEntity.created(location).body(createdId);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @Valid @RequestBody TaskRequestDto dto) {
        return ResponseEntity.ok(taskService.update(id, dto));
    }

//    public ResponseEntity<> deleteTask(@PathVariable TaskDto dto) {
//    }
//
//    public ResponseEntity<> getTasks() {
//    }

}
