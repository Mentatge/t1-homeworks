package ru.t1.homeworks.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import ru.t1.homeworks.task.entity.Task;
import ru.t1.homeworks.task.entity.TaskStatus;
import ru.t1.homeworks.task.service.dto.TaskDto;
import ru.t1.homeworks.task.service.dto.TaskRequestDto;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ModelMapperConfigTest {

    private final ModelMapper mapper = new ModelMapperConfig().modelMapper();

    @Test
    @DisplayName("Маппер должен правильно преобразовывать Task в TaskDto")
    void modelMapper() {
        Task task = new Task();
        task.setId(10L);
        task.setTitle("Title");
        task.setDescription("Description");
        task.setUserId(5L);
        task.setStatus(TaskStatus.IN_PROGRESS);
        task.setCreatedAt(LocalDate.of(2025, 1, 1));
        task.setUpdatedAt(LocalDate.of(2025, 1, 2));

        TaskDto dto = mapper.map(task, TaskDto.class);

        assertEquals(10L, dto.getId());
        assertEquals("Title", dto.getTitle());
        assertEquals("Description", dto.getDescription());
        assertEquals(5L, dto.getUserId());
        assertEquals(TaskStatus.IN_PROGRESS, dto.getStatus());
    }

    @Test
    @DisplayName("Маппер должен правильно преобразовывать TaskRequestDto в Task")
    void mapRequestDtoToTask() {
        TaskRequestDto request = new TaskRequestDto();
        request.setTitle("Title");
        request.setDescription("Description");
        request.setUserId(7L);
        request.setStatus(TaskStatus.COMPLETED);

        Task task = mapper.map(request, Task.class);

        assertNotNull(task.getId());
        assertEquals("Title", task.getTitle());
        assertEquals("Description", task.getDescription());
        assertEquals(7L, task.getUserId());
        assertEquals(TaskStatus.COMPLETED, task.getStatus());
        assertNull(task.getCreatedAt());
        assertNull(task.getUpdatedAt());
    }
}