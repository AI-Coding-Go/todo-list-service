package org.example.todolistservice.repository;

import org.example.todolistservice.entity.TodoTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 待办任务仓库接口
 */
@Repository
public interface TodoTaskRepository extends JpaRepository<TodoTask, Long> {

    /**
     * 根据状态查找任务
     */
    List<TodoTask> findByStatus(TodoTask.TaskStatus status);

    /**
     * 根据优先级查找任务
     */
    List<TodoTask> findByPriority(TodoTask.Priority priority);

    /**
     * 查找逾期未完成的任务
     */
    @Query("SELECT t FROM TodoTask t WHERE t.deadline < :now AND t.status = 'PENDING'")
    List<TodoTask> findOverduePendingTasks(@Param("now") LocalDateTime now);

    /**
     * 查找需要提醒的任务（截止时间前30分钟或整点）
     */
    @Query("SELECT t FROM TodoTask t WHERE t.deadline BETWEEN :startTime AND :endTime AND t.status = 'PENDING'")
    List<TodoTask> findTasksNeedingReminder(@Param("startTime") LocalDateTime startTime, 
                                           @Param("endTime") LocalDateTime endTime);

    /**
     * 查找逾期超过24小时未完成的任务（用于每日提醒）
     */
    @Query("SELECT t FROM TodoTask t WHERE t.deadline < :deadline AND t.status = 'PENDING'")
    List<TodoTask> findOverdueTasksForDailyReminder(@Param("deadline") LocalDateTime deadline);

    /**
     * 统计各状态任务数量
     */
    @Query("SELECT t.status, COUNT(t) FROM TodoTask t GROUP BY t.status")
    List<Object[]> countTasksByStatus();

    /**
     * 统计各优先级任务数量
     */
    @Query("SELECT t.priority, COUNT(t) FROM TodoTask t GROUP BY t.priority")
    List<Object[]> countTasksByPriority();

    /**
     * 统计近7天每日新增任务数
     */
    @Query(value = "SELECT DATE(t.created_at), COUNT(t.id) FROM todo_tasks t WHERE t.created_at >= :startDate GROUP BY DATE(t.created_at) ORDER BY DATE(t.created_at)", nativeQuery = true)
    List<Object[]> countDailyTasksCreated(@Param("startDate") LocalDateTime startDate);

    /**
     * 统计近7天每日完成任务数
     */
    @Query(value = "SELECT DATE(t.completed_at), COUNT(t.id) FROM todo_tasks t WHERE t.completed_at >= :startDate GROUP BY DATE(t.completed_at) ORDER BY DATE(t.completed_at)", nativeQuery = true)
    List<Object[]> countDailyTasksCompleted(@Param("startDate") LocalDateTime startDate);

    /**
     * 按创建时间倒序查找所有任务
     */
    List<TodoTask> findAllByOrderByCreatedAtDesc();

    /**
     * 按截止时间正序查找所有任务（无截止时间的排在最后）
     */
    @Query("SELECT t FROM TodoTask t ORDER BY CASE WHEN t.deadline IS NULL THEN 1 ELSE 0 END, t.deadline ASC")
    List<TodoTask> findAllByOrderByDeadlineAscNullsLast();

    /**
     * 按优先级和创建时间排序查找所有任务
     */
    @Query("SELECT t FROM TodoTask t ORDER BY CASE t.priority WHEN 'HIGH' THEN 1 WHEN 'MEDIUM' THEN 2 WHEN 'LOW' THEN 3 END, t.createdAt DESC")
    List<TodoTask> findAllByOrderByPriorityAndCreatedAtDesc();
}