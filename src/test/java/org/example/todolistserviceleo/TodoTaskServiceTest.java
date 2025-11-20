package org.example.todolistserviceleo;

import org.example.todolistserviceleo.dto.TodoTaskCreateRequest;
import org.example.todolistserviceleo.dto.TodoTaskUpdateRequest;
import org.example.todolistserviceleo.entity.TodoTask;
import org.example.todolistserviceleo.repository.TodoTaskRepository;
import org.example.todolistserviceleo.service.TodoTaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TodoTaskServiceTest {

    @Mock
    private TodoTaskRepository todoTaskRepository;

    @InjectMocks
    private TodoTaskService todoTaskService;

    private TodoTask testTask;

    @BeforeEach
    void setUp() {
        testTask = new TodoTask();
        testTask.setId(1L);
        testTask.setTitle("测试任务");
        testTask.setDescription("这是一个测试任务");
        testTask.setPriority(TodoTask.Priority.MEDIUM);
        testTask.setStatus(TodoTask.TaskStatus.PENDING);
        testTask.setCreatedAt(LocalDateTime.now());
        testTask.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void testCreateTask() {
        // 准备测试数据
        TodoTaskCreateRequest request = new TodoTaskCreateRequest();
        request.setTitle("新任务");
        request.setDescription("任务描述");
        request.setPriority("HIGH");
        request.setDeadline(LocalDateTime.now().plusDays(1));

        when(todoTaskRepository.save(any(TodoTask.class))).thenReturn(testTask);

        // 执行测试
        var result = todoTaskService.createTask(request);

        // 验证结果
        assertNotNull(result);
        verify(todoTaskRepository, times(1)).save(any(TodoTask.class));
    }

    @Test
    void testUpdateTask() {
        // 准备测试数据
        TodoTaskUpdateRequest request = new TodoTaskUpdateRequest();
        request.setTitle("更新后的任务");
        request.setDescription("更新后的描述");
        request.setPriority("LOW");

        when(todoTaskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        when(todoTaskRepository.save(any(TodoTask.class))).thenReturn(testTask);

        // 执行测试
        var result = todoTaskService.updateTask(1L, request);

        // 验证结果
        assertNotNull(result);
        verify(todoTaskRepository, times(1)).findById(1L);
        verify(todoTaskRepository, times(1)).save(any(TodoTask.class));
    }

    @Test
    void testDeleteTask() {
        when(todoTaskRepository.findById(1L)).thenReturn(Optional.of(testTask));

        // 执行测试
        todoTaskService.deleteTask(1L);

        // 验证结果
        verify(todoTaskRepository, times(1)).findById(1L);
        verify(todoTaskRepository, times(1)).delete(testTask);
    }

    @Test
    void testToggleTaskStatus() {
        when(todoTaskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        when(todoTaskRepository.save(any(TodoTask.class))).thenReturn(testTask);

        // 执行测试
        var result = todoTaskService.toggleTaskStatus(1L);

        // 验证结果
        assertNotNull(result);
        verify(todoTaskRepository, times(1)).findById(1L);
        verify(todoTaskRepository, times(1)).save(any(TodoTask.class));
    }

    @Test
    void testGetTask() {
        when(todoTaskRepository.findById(1L)).thenReturn(Optional.of(testTask));

        // 执行测试
        var result = todoTaskService.getTask(1L);

        // 验证结果
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("测试任务", result.getTitle());
        verify(todoTaskRepository, times(1)).findById(1L);
    }

    @Test
    void testGetAllTasks() {
        List<TodoTask> tasks = Arrays.asList(testTask);
        when(todoTaskRepository.findAllByOrderByCreatedAtDesc()).thenReturn(tasks);

        // 执行测试
        var result = todoTaskService.getAllTasks("created");

        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(todoTaskRepository, times(1)).findAllByOrderByCreatedAtDesc();
    }

    @Test
    void testGetTasksByStatus() {
        List<TodoTask> tasks = Arrays.asList(testTask);
        when(todoTaskRepository.findByStatus(TodoTask.TaskStatus.PENDING)).thenReturn(tasks);

        // 执行测试
        var result = todoTaskService.getTasksByStatus("pending", "created");

        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(todoTaskRepository, times(1)).findByStatus(TodoTask.TaskStatus.PENDING);
    }

    @Test
    void testGetTaskStatistics() {
        when(todoTaskRepository.countTasksByStatus()).thenReturn(Arrays.asList(
            new Object[]{"PENDING", 5L},
            new Object[]{"COMPLETED", 3L}
        ));
        when(todoTaskRepository.countTasksByPriority()).thenReturn(Arrays.asList(
            new Object[]{"HIGH", 2L},
            new Object[]{"MEDIUM", 4L},
            new Object[]{"LOW", 2L}
        ));

        // 执行测试
        var result = todoTaskService.getTaskStatistics();

        // 验证结果
        assertNotNull(result);
        assertTrue(result.containsKey("statusCount"));
        assertTrue(result.containsKey("priorityCount"));
        assertTrue(result.containsKey("completionRate"));
        verify(todoTaskRepository, times(1)).countTasksByStatus();
        verify(todoTaskRepository, times(1)).countTasksByPriority();
    }
}