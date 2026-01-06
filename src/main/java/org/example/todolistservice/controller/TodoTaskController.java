package org.example.todolistservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.todolistservice.dto.TodoTaskCreateRequest;
import org.example.todolistservice.dto.TodoTaskResponse;
import org.example.todolistservice.dto.TodoTaskUpdateRequest;
import org.example.todolistservice.service.TodoTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 待办任务控制器
 */
@RestController
@RequestMapping("/api/tasks")
@Tag(name = "待办任务管理", description = "待办任务的增删改查和统计功能")
@Validated
public class TodoTaskController {

    @Autowired
    private TodoTaskService todoTaskService;

    /**
     * 创建新任务
     */
    @PostMapping
    @Operation(summary = "创建任务", description = "创建一个新的待办任务")
    public ResponseEntity<TodoTaskResponse> createTask(@Valid @RequestBody TodoTaskCreateRequest request) {
        TodoTaskResponse response = todoTaskService.createTask(request);
        return ResponseEntity.ok(response);
    }

    /**
     * 更新任务
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新任务", description = "更新指定ID的任务信息")
    public ResponseEntity<TodoTaskResponse> updateTask(
            @Parameter(description = "任务ID") @PathVariable Long id,
            @Valid @RequestBody TodoTaskUpdateRequest request) {
        TodoTaskResponse response = todoTaskService.updateTask(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 删除任务
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除任务", description = "删除指定ID的任务")
    public ResponseEntity<Void> deleteTask(@Parameter(description = "任务ID") @PathVariable Long id) {
        todoTaskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 标记任务完成/未完成
     */
    @PatchMapping("/{id}/toggle")
    @Operation(summary = "切换任务状态", description = "切换任务的完成/未完成状态")
    public ResponseEntity<TodoTaskResponse> toggleTaskStatus(@Parameter(description = "任务ID") @PathVariable Long id) {
        TodoTaskResponse response = todoTaskService.toggleTaskStatus(id);
        return ResponseEntity.ok(response);
    }

    /**
     * 获取单个任务详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取任务详情", description = "根据ID获取任务的详细信息")
    public ResponseEntity<TodoTaskResponse> getTask(@Parameter(description = "任务ID") @PathVariable Long id) {
        TodoTaskResponse response = todoTaskService.getTask(id);
        return ResponseEntity.ok(response);
    }

    /**
     * 获取任务列表
     */
    @GetMapping
    @Operation(summary = "获取任务列表", description = "获取任务列表，支持状态筛选和排序")
    public ResponseEntity<List<TodoTaskResponse>> getTasks(
            @Parameter(description = "任务状态筛选：all(全部), pending(未完成), completed(已完成)") 
            @RequestParam(defaultValue = "all") String status,
            @Parameter(description = "排序方式：created(创建时间), deadline(截止时间), priority(优先级)") 
            @RequestParam(defaultValue = "created") String sortBy) {
        
        List<TodoTaskResponse> tasks;
        if ("all".equals(status)) {
            tasks = todoTaskService.getAllTasks(sortBy);
        } else {
            tasks = todoTaskService.getTasksByStatus(status, sortBy);
        }
        
        return ResponseEntity.ok(tasks);
    }

    /**
     * 获取任务统计信息
     */
    @GetMapping("/statistics")
    @Operation(summary = "获取任务统计", description = "获取任务的统计数据，包括完成率、优先级分布等")
    public ResponseEntity<Map<String, Object>> getTaskStatistics() {
        Map<String, Object> statistics = todoTaskService.getTaskStatistics();
        return ResponseEntity.ok(statistics);
    }

    /**
     * 获取需要提醒的任务
     */
    @GetMapping("/reminders")
    @Operation(summary = "获取提醒任务", description = "获取需要提醒的任务列表")
    public ResponseEntity<List<TodoTaskResponse>> getTasksNeedingReminder() {
        List<TodoTaskResponse> tasks = todoTaskService.getTasksNeedingReminder();
        return ResponseEntity.ok(tasks);
    }

    /**
     * 获取逾期任务
     */
    @GetMapping("/overdue")
    @Operation(summary = "获取逾期任务", description = "获取所有逾期未完成的任务")
    public ResponseEntity<List<TodoTaskResponse>> getOverdueTasks() {
        List<TodoTaskResponse> tasks = todoTaskService.getOverdueTasks();
        return ResponseEntity.ok(tasks);
    }
}