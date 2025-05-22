package ru.t1.homeworks.task.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.kafka.core.KafkaTemplate;
import ru.t1.homeworks.task.dao.TaskRepository;
import ru.t1.homeworks.task.entity.Task;
import ru.t1.homeworks.task.entity.TaskStatus;
import ru.t1.homeworks.task.exception.TaskNotFoundException;
import ru.t1.homeworks.task.service.dto.TaskDto;
import ru.t1.homeworks.task.service.dto.TaskRequestDto;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private KafkaTemplate<Long, String> kafkaTemplate;

    @InjectMocks
    private TaskServiceImpl taskService;

    private Task task;
    private TaskDto taskDto;
    private TaskRequestDto requestDto;

    @BeforeEach
    void setUp() {
        task = new Task();
        task.setId(1L);
        task.setTitle("title");
        task.setDescription("desc");
        task.setUserId(2L);
        task.setStatus(TaskStatus.NEW);

        taskDto = new TaskDto();
        taskDto.setId(1L);
        taskDto.setTitle("title");
        taskDto.setDescription("desc");
        taskDto.setUserId(2L);
        taskDto.setStatus(TaskStatus.NEW);

        requestDto = new TaskRequestDto();
        requestDto.setTitle("title");
        requestDto.setDescription("desc");
        requestDto.setUserId(2L);
        requestDto.setStatus(TaskStatus.NEW);
    }

    @Test
    @DisplayName("Метод getById должен вернуть TaskDto по id")
    void getById() {
        when(taskRepository.findById(any())).thenReturn(Optional.of(task));
        when(modelMapper.map(task, TaskDto.class)).thenReturn(taskDto);

        TaskDto dto = taskService.getById(1L);

        assertEquals(dto, taskDto);
        verify(taskRepository).findById(1L);
    }


    @Test
    @DisplayName("Метод getById должен вернуть ошибку если не не найдет в репозитории задачу")
    void getById_throwsException() {
        when(taskRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(TaskNotFoundException.class, () -> taskService.getById(1L));
    }

    @Test
    @DisplayName("Метод create должен успешно создать task и вернуть его айди")
    void create() {
        Task toSave = new Task();
        when(modelMapper.map(requestDto, Task.class)).thenReturn(toSave);
        Task saved = new Task();
        saved.setId(5L);
        when(taskRepository.save(toSave)).thenReturn(saved);

        Long id = taskService.create(requestDto);

        assertEquals(5L, id);
        verify(taskRepository).save(toSave);
    }

    @Test
    @DisplayName("Метод update не должен отправлять сообщение в Kafka, если статус не изменился")
    void updateNotSendKafka() {
        Task existing = new Task();
        existing.setId(1L);
        existing.setStatus(TaskStatus.NEW);
        when(taskRepository.findById(any())).thenReturn(Optional.of(existing));
        requestDto.setStatus(TaskStatus.NEW);
        when(taskRepository.save(existing)).thenReturn(existing);
        taskService.update(1L, requestDto);
        verify(kafkaTemplate, never()).send(anyString(), anyLong(), anyString());
    }

    @Test
    @DisplayName("Метод update должен отправлять сообщение в Kafka, если статус изменился")
    void updateSendKafka() {
        Task existing = new Task();
        existing.setId(1L);
        existing.setStatus(TaskStatus.NEW);
        when(taskRepository.findById(any())).thenReturn(Optional.of(existing));
        requestDto.setStatus(TaskStatus.COMPLETED);
        when(taskRepository.save(existing)).thenReturn(existing);

        taskService.update(1L, requestDto);

        verify(taskRepository).save(existing);
    }

    @Test
    @DisplayName("Метод update должен бросать TaskNotFoundException, если задачи нет")
    void updateThrowsException() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(TaskNotFoundException.class,
                () -> taskService.update(1L, requestDto));
    }

    @Test
    @DisplayName("Метод delete должен удалить задачу по id")
    void delete() {
        when(taskRepository.findById(any())).thenReturn(Optional.of(task));

        taskService.delete(1L);

        verify(taskRepository).delete(task);
    }

    @Test
    @DisplayName("Метод delete должен бросать TaskNotFoundException, если задачи нет")
    void deleteThrowsException() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(TaskNotFoundException.class,
                () -> taskService.delete(1L));
    }

    @Test
    @DisplayName("Метод getTasks должен вернуть список TaskDto")
    void getTasks() {
        List<Task> tasks = List.of(task);
        when(taskRepository.findAll()).thenReturn(tasks);
        when(modelMapper.map(task, TaskDto.class)).thenReturn(taskDto);

        List<TaskDto> dto = taskService.getTasks();

        assertEquals(1, dto.size());
        assertEquals(taskDto, dto.get(0));
    }
}