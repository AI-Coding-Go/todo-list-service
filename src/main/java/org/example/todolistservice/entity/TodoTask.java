package org.example.todolistservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * 待办任务实体类
 */
@Entity
@Table(name = "todo_tasks")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TodoTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 任务标题（必填，限50字以内）
     */
    @NotBlank(message = "请输入任务标题")
    @Size(max = 50, message = "任务标题不能超过50个字符")
    @Column(nullable = false, length = 50)
    private String title;

    /**
     * 任务描述（可选，限500字以内）
     */
    @Size(max = 500, message = "任务描述不能超过500个字符")
    @Column(length = 500)
    private String description;

    /**
     * 任务状态（默认未完成）
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status = TaskStatus.PENDING;

    /**
     * 优先级（默认中）
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Priority priority = Priority.MEDIUM;

    /**
     * 截止时间（可选）
     */
    @Column
    private LocalDateTime deadline;

    /**
     * 创建时间
     */
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * 完成时间
     */
    @Column
    private LocalDateTime completedAt;

    /**
     * 任务状态枚举
     */
    public enum TaskStatus {
        PENDING("未完成"),
        COMPLETED("已完成");

        private final String description;

        TaskStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 优先级枚举
     */
    public enum Priority {
        HIGH("高", "#FF4444"),
        MEDIUM("中", "#888888"),
        LOW("低", "#44FF44");

        private final String description;
        private final String color;

        Priority(String description, String color) {
            this.description = description;
            this.color = color;
        }

        public String getDescription() {
            return description;
        }

        public String getColor() {
            return color;
        }
    }

    /**
     * 标记任务为已完成
     */
    public void markAsCompleted() {
        this.status = TaskStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
    }

    /**
     * 标记任务为未完成
     */
    public void markAsPending() {
        this.status = TaskStatus.PENDING;
        this.completedAt = null;
    }

    /**
     * 检查任务是否逾期
     */
    public boolean isOverdue() {
        return deadline != null && deadline.isBefore(LocalDateTime.now()) && status == TaskStatus.PENDING;
    }

    /**
     * 获取剩余时间（分钟）
     */
    public Long getRemainingMinutes() {
        if (deadline == null) {
            return null;
        }
        return java.time.Duration.between(LocalDateTime.now(), deadline).toMinutes();
    }
}