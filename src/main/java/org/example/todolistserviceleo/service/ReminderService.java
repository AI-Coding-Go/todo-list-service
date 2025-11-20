package org.example.todolistserviceleo.service;

import org.example.todolistserviceleo.dto.TodoTaskResponse;
import org.example.todolistserviceleo.entity.TodoTask;
import org.example.todolistserviceleo.repository.TodoTaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 任务提醒服务
 */
@Service
public class ReminderService {

    private static final Logger logger = LoggerFactory.getLogger(ReminderService.class);

    @Autowired
    private TodoTaskRepository todoTaskRepository;

    // 记录已提醒的任务，避免重复提醒
    private final Map<Long, Integer> reminderCountMap = new ConcurrentHashMap<>();

    /**
     * 每分钟检查一次需要提醒的任务
     */
    @Scheduled(cron = "0 * * * * ?")
    public void checkTaskReminders() {
        logger.info("开始检查任务提醒...");
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime thirtyMinutesLater = now.plusMinutes(30);
        
        // 查找需要提醒的任务（截止时间前30分钟内）
        List<TodoTask> tasksNeedingReminder = todoTaskRepository.findTasksNeedingReminder(now, thirtyMinutesLater);
        
        for (TodoTask task : tasksNeedingReminder) {
            // 检查是否已经提醒过
            if (!reminderCountMap.containsKey(task.getId())) {
                sendReminder(task, "截止时间提醒");
                reminderCountMap.put(task.getId(), 1);
            }
        }
        
        // 检查整点提醒
        if (now.getMinute() == 0) {
            checkHourlyReminders(now);
        }
        
        // 检查逾期提醒（每24小时一次）
        checkOverdueReminders(now);
        
        logger.info("任务提醒检查完成，共检查到 {} 个需要提醒的任务", tasksNeedingReminder.size());
    }

    /**
     * 检查整点提醒
     */
    private void checkHourlyReminders(LocalDateTime now) {
        LocalDateTime oneHourLater = now.plusHours(1);
        List<TodoTask> tasks = todoTaskRepository.findTasksNeedingReminder(now, oneHourLater);
        
        for (TodoTask task : tasks) {
            // 检查30分钟前是否已经提醒过
            Integer reminderCount = reminderCountMap.get(task.getId());
            if (reminderCount == null || reminderCount < 2) {
                sendReminder(task, "整点提醒");
                reminderCountMap.put(task.getId(), 2);
            }
        }
    }

    /**
     * 检查逾期提醒（最多提醒3次）
     */
    private void checkOverdueReminders(LocalDateTime now) {
        LocalDateTime twentyFourHoursAgo = now.minusHours(24);
        List<TodoTask> overdueTasks = todoTaskRepository.findOverdueTasksForDailyReminder(twentyFourHoursAgo);
        
        for (TodoTask task : overdueTasks) {
            Integer reminderCount = reminderCountMap.getOrDefault(task.getId(), 0);
            
            // 最多提醒3次
            if (reminderCount < 3) {
                sendReminder(task, "逾期提醒");
                reminderCountMap.put(task.getId(), reminderCount + 1);
            }
        }
    }

    /**
     * 发送提醒
     */
    private void sendReminder(TodoTask task, String reminderType) {
        logger.info("发送{} - 任务ID: {}, 标题: {}, 截止时间: {}", 
                   reminderType, task.getId(), task.getTitle(), task.getDeadline());
        
        // 这里可以集成实际的提醒方式：
        // 1. WebSocket推送到前端
        // 2. 发送邮件
        // 3. 发送短信
        // 4. 推送通知到移动端
        
        // 目前只记录日志
        System.out.println("=== 任务提醒 ===");
        System.out.println("提醒类型: " + reminderType);
        System.out.println("任务标题: " + task.getTitle());
        System.out.println("任务描述: " + (task.getDescription() != null ? task.getDescription() : "无"));
        System.out.println("截止时间: " + task.getDeadline());
        System.out.println("优先级: " + task.getPriority().getDescription());
        System.out.println("================");
    }

    /**
     * 清理已完成任务的提醒记录
     */
    @Scheduled(cron = "0 0 2 * * ?") // 每天凌晨2点执行
    public void cleanReminderRecords() {
        logger.info("开始清理已完成任务的提醒记录...");
        
        List<TodoTask> completedTasks = todoTaskRepository.findByStatus(TodoTask.TaskStatus.COMPLETED);
        
        for (TodoTask task : completedTasks) {
            reminderCountMap.remove(task.getId());
        }
        
        logger.info("清理完成，移除了 {} 个已完成任务的提醒记录", completedTasks.size());
    }

    /**
     * 获取当前需要提醒的任务列表
     */
    public List<TodoTaskResponse> getCurrentReminders() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime thirtyMinutesLater = now.plusMinutes(30);
        
        List<TodoTask> tasks = todoTaskRepository.findTasksNeedingReminder(now, thirtyMinutesLater);
        
        return tasks.stream()
                .map(TodoTaskResponse::fromEntity)
                .collect(java.util.stream.Collectors.toList());
    }
}