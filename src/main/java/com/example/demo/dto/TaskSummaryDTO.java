package com.example.demo.dto;

import com.example.demo.model.Task;
import com.example.demo.model.enums.TaskStatus;
import com.example.demo.model.enums.TaskType;
import java.time.LocalDateTime;

public class TaskSummaryDTO {

    private Long taskId;
    private String title;
    private TaskType taskType;
    private TaskStatus status;
    private LocalDateTime dueDate;
    private LocalDateTime createdAt;
    private UserSummaryDTO creator; // 复用之前创建的 UserSummaryDTO

    // 构造函数，用于从 Task 实体转换
    public TaskSummaryDTO(Task task) {
        this.taskId = task.getTaskId();
        this.title = task.getTitle();
        this.taskType = task.getTaskType();
        this.status = task.getStatus();
        this.dueDate = task.getDueDate();
        this.createdAt = task.getCreatedAt();

        // 创建创建者的摘要信息
        if (task.getCreator() != null) {
            this.creator = new UserSummaryDTO(
                    task.getCreator().getUserId(),
                    task.getCreator().getUsername(),
                    task.getCreator().getEmail(),
                    task.getCreator().getIntroduction()
            );
        }
    }

    // --- Getters and Setters ---

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public void setTaskType(TaskType taskType) {
        this.taskType = taskType;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public UserSummaryDTO getCreator() {
        return creator;
    }

    public void setCreator(UserSummaryDTO creator) {
        this.creator = creator;
    }
}