package org.example.todolistserviceleo.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 更新任务请求DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TodoTaskUpdateRequest {

    /**
     * 任务标题（必填，限50字以内）
     */
    @NotBlank(message = "请输入任务标题")
    @Size(max = 50, message = "任务标题不能超过50个字符")
    private String title;

    /**
     * 任务描述（可选，限500字以内）
     */
    @Size(max = 500, message = "任务描述不能超过500个字符")
    private String description;

    /**
     * 优先级
     */
    private String priority;

    /**
     * 截止时间（可选）
     */
    private LocalDateTime deadline;
}