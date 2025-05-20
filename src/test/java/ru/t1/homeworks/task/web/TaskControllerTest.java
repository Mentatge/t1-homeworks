package ru.t1.homeworks.task.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.t1.homeworks.task.dao.TaskRepository;
import ru.t1.homeworks.task.entity.Task;
import ru.t1.homeworks.task.entity.TaskStatus;
import ru.t1.homeworks.task.service.dto.TaskRequestDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
class TaskControllerTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:13-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void registerDbProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();
    }

    @Test
    @DisplayName("Интеграционный тест: получить список задач")
    void getTask() throws Exception {
        Task t1 = new Task();
        t1.setTitle("Task1");
        t1.setDescription("Desc1");
        t1.setUserId(1L);
        t1.setStatus(TaskStatus.NEW);

        Task t2 = new Task();
        t2.setTitle("Task2");
        t2.setDescription("Desc2");
        t2.setUserId(2L);
        t2.setStatus(TaskStatus.IN_PROGRESS);

        taskRepository.saveAll(List.of(t1, t2));

        mockMvc.perform(get("/api/v1/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Task1"))
                .andExpect(jsonPath("$[1].title").value("Task2"));
    }

    @Test
    @DisplayName("Интеграционный тест: создать и получить задачу")
    void createTask() throws Exception {
        TaskRequestDto dto = new TaskRequestDto();
        dto.setTitle("Integration Test");
        dto.setDescription("desc");
        dto.setUserId(1L);
        dto.setStatus(TaskStatus.NEW);

        String json = objectMapper.writeValueAsString(dto);

        mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());

        List<Task> tasks = taskRepository.findAll();
        assertFalse(tasks.isEmpty());

        Long id = tasks.get(0).getId();
        mockMvc.perform(get("/api/v1/tasks/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Integration Test"))
                .andExpect(jsonPath("$.status").value("NEW"));
    }

    @Test
    @DisplayName("Интеграционный тест: создание задачи без userId возвращает 400")
    void createTaskBadRequest() throws Exception {
        String badJson = "{\"title\":\"Test\",\"description\":\"desc\",\"status\":\"NEW\"}";

        mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(badJson))
                .andExpect(status().isBadRequest());
    }


    @Test
    @DisplayName("Интеграционный тест: успешное обновление задачи")
    void updateTask() throws Exception {
        Task saved = new Task();
        saved.setTitle("Old");
        saved.setDescription("Old");
        saved.setUserId(1L);
        saved.setStatus(TaskStatus.NEW);
        saved = taskRepository.save(saved);

        TaskRequestDto dto = new TaskRequestDto();
        dto.setTitle("Updated");
        dto.setDescription("Updated");
        dto.setUserId(1L);
        dto.setStatus(TaskStatus.COMPLETED);

        String json = objectMapper.writeValueAsString(dto);

        mockMvc.perform(put("/api/v1/tasks/" + saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated"))
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    @DisplayName("Интеграционный тест: обновление несуществующей задачи возвращает 404")
    void updateTaskBadRequest() throws Exception {
        TaskRequestDto dto = new TaskRequestDto();
        dto.setTitle("test");
        dto.setDescription("desc");
        dto.setUserId(1L);
        dto.setStatus(TaskStatus.NEW);

        String json = objectMapper.writeValueAsString(dto);

        mockMvc.perform(put("/api/v1/tasks/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Интеграционный тест: удаление задачи")
    void deleteTask() throws Exception {
        Task task = new Task();
        task.setTitle("To Delete");
        task.setDescription("desc");
        task.setUserId(1L);
        task.setStatus(TaskStatus.NEW);
        task = taskRepository.save(task);

        mockMvc.perform(delete("/api/v1/tasks/" + task.getId()))
                .andExpect(status().isOk());

        assertTrue(taskRepository.findById(task.getId()).isEmpty());

    }
}