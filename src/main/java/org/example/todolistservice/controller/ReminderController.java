package org.example.todolistservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.todolistservice.dto.TodoTaskResponse;
import org.example.todolistservice.service.ReminderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 提醒控制器
 */
@RestController
@RequestMapping("/api/reminders")
@Tag(name = "任务提醒", description = "任务提醒相关功能")
public class ReminderController {

    @Autowired
    private ReminderService reminderService;

    /**
     * 获取当前需要提醒的任务
     */
    @GetMapping("/current")
    @Operation(summary = "获取当前提醒", description = "获取当前需要提醒的任务列表")
    public ResponseEntity<List<TodoTaskResponse>> getCurrentReminders() {
        List<TodoTaskResponse> tasks = reminderService.getCurrentReminders();
        return ResponseEntity.ok(tasks);
    }
}