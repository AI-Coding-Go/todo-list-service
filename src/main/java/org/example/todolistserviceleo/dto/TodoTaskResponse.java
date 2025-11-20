package org.example.todolistserviceleo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 任务响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TodoTaskResponse {

    private Long id;
    private String title;
    private String description;
    private String status;
    private String statusDescription;
    private String priority;
    private String priorityDescription;
    private String priorityColor;
    private LocalDateTime deadline;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime completedAt;
    private boolean overdue;
    private Long remainingMinutes;
    private String deadlineFormatted;
    private String completedAtFormatted;

    public static TodoTaskResponse fromEntity(org.example.todolistserviceleo.entity.TodoTask task) {
        TodoTaskResponse response = new TodoTaskResponse();
        response.setId(task.getId());
        response.setTitle(task.getTitle());
        response.setDescription(task.getDescription());
        response.setStatus(task.getStatus().name());
        response.setStatusDescription(task.getStatus().getDescription());
        response.setPriority(task.getPriority().name());
        response.setPriorityDescription(task.getPriority().getDescription());
        response.setPriorityColor(task.getPriority().getColor());
        response.setDeadline(task.getDeadline());
        response.setCreatedAt(task.getCreatedAt());
        response.setUpdatedAt(task.getUpdatedAt());
        response.setCompletedAt(task.getCompletedAt());
        response.setOverdue(task.isOverdue());
        response.setRemainingMinutes(task.getRemainingMinutes());
        
        // 格式化截止时间
        if (task.getDeadline() != null) {
            response.setDeadlineFormatted(task.getDeadline().format(
                java.time.format.DateTimeFormatter.ofPattern("MM-dd HH:mm")));
        }
        
        // 格式化完成时间
        if (task.getCompletedAt() != null) {
            response.setCompletedAtFormatted(task.getCompletedAt().format(
                java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        }
        
        return response;
    }
}