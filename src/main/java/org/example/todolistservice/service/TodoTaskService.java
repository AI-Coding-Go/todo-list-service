package org.example.todolistservice.service;

import org.example.todolistservice.dto.TodoTaskCreateRequest;
import org.example.todolistservice.dto.TodoTaskResponse;
import org.example.todolistservice.dto.TodoTaskUpdateRequest;
import org.example.todolistservice.entity.TodoTask;
import org.example.todolistservice.repository.TodoTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 待办任务服务类
 */
@Service
@Transactional
public class TodoTaskService {

    @Autowired
    private TodoTaskRepository todoTaskRepository;

    /**
     * 创建新任务
     */
    public TodoTaskResponse createTask(TodoTaskCreateRequest request) {
        TodoTask task = new TodoTask();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setPriority(TodoTask.Priority.valueOf(request.getPriority()));
        task.setDeadline(request.getDeadline());
        
        TodoTask savedTask = todoTaskRepository.save(task);
        return TodoTaskResponse.fromEntity(savedTask);
    }

    /**
     * 更新任务
     */
    public TodoTaskResponse updateTask(Long id, TodoTaskUpdateRequest request) {
        TodoTask task = getTaskById(id);
        
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setPriority(TodoTask.Priority.valueOf(request.getPriority()));
        task.setDeadline(request.getDeadline());
        
        TodoTask updatedTask = todoTaskRepository.save(task);
        return TodoTaskResponse.fromEntity(updatedTask);
    }

    /**
     * 删除任务
     */
    public void deleteTask(Long id) {
        TodoTask task = getTaskById(id);
        todoTaskRepository.delete(task);
    }

    /**
     * 标记任务完成/未完成
     */
    public TodoTaskResponse toggleTaskStatus(Long id) {
        TodoTask task = getTaskById(id);
        
        if (task.getStatus() == TodoTask.TaskStatus.PENDING) {
            task.markAsCompleted();
        } else {
            task.markAsPending();
        }
        
        TodoTask updatedTask = todoTaskRepository.save(task);
        return TodoTaskResponse.fromEntity(updatedTask);
    }

    /**
     * 根据ID获取任务
     */
    @Transactional(readOnly = true)
    public TodoTaskResponse getTask(Long id) {
        TodoTask task = getTaskById(id);
        return TodoTaskResponse.fromEntity(task);
    }

    /**
     * 获取所有任务（按创建时间倒序）
     */
    @Transactional(readOnly = true)
    public List<TodoTaskResponse> getAllTasks(String sortBy) {
        List<TodoTask> tasks;
        
        switch (sortBy) {
            case "deadline":
                tasks = todoTaskRepository.findAllByOrderByDeadlineAscNullsLast();
                break;
            case "priority":
                tasks = todoTaskRepository.findAllByOrderByPriorityAndCreatedAtDesc();
                break;
            default: // "created" or null
                tasks = todoTaskRepository.findAllByOrderByCreatedAtDesc();
                break;
        }
        
        return tasks.stream()
                .map(TodoTaskResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 根据状态筛选任务
     */
    @Transactional(readOnly = true)
    public List<TodoTaskResponse> getTasksByStatus(String status, String sortBy) {
        List<TodoTask> tasks;
        
        if ("completed".equals(status)) {
            tasks = todoTaskRepository.findByStatus(TodoTask.TaskStatus.COMPLETED);
        } else if ("pending".equals(status)) {
            tasks = todoTaskRepository.findByStatus(TodoTask.TaskStatus.PENDING);
        } else {
            return getAllTasks(sortBy);
        }
        
        // 应用排序
        tasks = applySorting(tasks, sortBy);
        
        return tasks.stream()
                .map(TodoTaskResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 获取任务统计信息
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getTaskStatistics() {
        // 状态统计
        List<Object[]> statusStats = todoTaskRepository.countTasksByStatus();
        Map<String, Long> statusCount = statusStats.stream()
                .filter(arr -> arr[0] != null && arr[1] != null)
                .collect(Collectors.toMap(
                        arr -> arr[0].toString(),
                        arr -> (Long) arr[1]
                ));
        
        // 优先级统计
        List<Object[]> priorityStats = todoTaskRepository.countTasksByPriority();
        Map<String, Long> priorityCount = priorityStats.stream()
                .filter(arr -> arr[0] != null && arr[1] != null)
                .collect(Collectors.toMap(
                        arr -> arr[0].toString(),
                        arr -> (Long) arr[1]
                ));
        
        // 近7天趋势统计
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        List<Object[]> createdStats = todoTaskRepository.countDailyTasksCreated(sevenDaysAgo);
        List<Object[]> completedStats = todoTaskRepository.countDailyTasksCompleted(sevenDaysAgo);
        
        Map<String, Long> dailyCreated = createdStats.stream()
                .filter(arr -> arr[0] != null && arr[1] != null)
                .collect(Collectors.toMap(
                        arr -> arr[0].toString(),
                        arr -> (Long) arr[1]
                ));
        
        Map<String, Long> dailyCompleted = completedStats.stream()
                .filter(arr -> arr[0] != null && arr[1] != null)
                .collect(Collectors.toMap(
                        arr -> arr[0].toString(),
                        arr -> (Long) arr[1]
                ));
        
        // 计算完成率
        long totalTasks = statusCount.values().stream().mapToLong(Long::longValue).sum();
        long completedTasks = statusCount.getOrDefault("COMPLETED", 0L);
        double completionRate = totalTasks > 0 ? (double) completedTasks / totalTasks * 100 : 0;
        
        return Map.of(
                "statusCount", statusCount,
                "priorityCount", priorityCount,
                "dailyCreated", dailyCreated,
                "dailyCompleted", dailyCompleted,
                "completionRate", Math.round(completionRate * 100.0) / 100.0,
                "totalTasks", totalTasks,
                "completedTasks", completedTasks
        );
    }

    /**
     * 获取需要提醒的任务
     */
    @Transactional(readOnly = true)
    public List<TodoTaskResponse> getTasksNeedingReminder() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime thirtyMinutesLater = now.plusMinutes(30);
        
        // 获取截止时间前30分钟内的任务
        List<TodoTask> tasks = todoTaskRepository.findTasksNeedingReminder(now, thirtyMinutesLater);
        
        return tasks.stream()
                .map(TodoTaskResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 获取逾期任务
     */
    @Transactional(readOnly = true)
    public List<TodoTaskResponse> getOverdueTasks() {
        List<TodoTask> tasks = todoTaskRepository.findOverduePendingTasks(LocalDateTime.now());
        return tasks.stream()
                .map(TodoTaskResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 根据ID获取任务实体（内部方法）
     */
    private TodoTask getTaskById(Long id) {
        return todoTaskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("任务不存在，ID: " + id));
    }

    /**
     * 应用排序
     */
    private List<TodoTask> applySorting(List<TodoTask> tasks, String sortBy) {
        switch (sortBy) {
            case "deadline":
                return tasks.stream()
                        .sorted((a, b) -> {
                            if (a.getDeadline() == null && b.getDeadline() == null) return 0;
                            if (a.getDeadline() == null) return 1;
                            if (b.getDeadline() == null) return -1;
                            return a.getDeadline().compareTo(b.getDeadline());
                        })
                        .collect(Collectors.toList());
            case "priority":
                return tasks.stream()
                        .sorted((a, b) -> {
                            int priorityCompare = a.getPriority().ordinal() - b.getPriority().ordinal();
                            if (priorityCompare != 0) return priorityCompare;
                            return b.getCreatedAt().compareTo(a.getCreatedAt());
                        })
                        .collect(Collectors.toList());
            default: // "created"
                return tasks.stream()
                        .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                        .collect(Collectors.toList());
        }
    }
}