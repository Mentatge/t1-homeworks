package ru.t1.homeworks.task.web;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.t1.homeworks.task.service.TaskService;
import ru.t1.homeworks.task.service.dto.TaskDto;

@RestController
@RequestMapping("api/v1/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @GetMapping("/{id}")
    public ResponseEntity<TaskDto> getTask(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.getById(id));
    }

    public ResponseEntity<> createTask(@PathVariable TaskDto dto) {
        return ResponseEntity.ok(taskService.create(dto));
    }

//    public ResponseEntity<> updateTask(@PathVariable TaskDto dto) {
//        return ResponseEntity.ok(taskService.update(dto));
//    }
//
//    public ResponseEntity<> deleteTask(@PathVariable TaskDto dto) {}
//    }

}
