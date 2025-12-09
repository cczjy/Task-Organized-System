package com.example.demo.dto;

import com.example.demo.model.Task;
import com.example.demo.model.enums.TaskStatus;
import com.example.demo.model.enums.TaskType;
import java.time.LocalDateTime;

public class UserTaskSummaryDTO {

    private Long taskId;
    private String title;
    private TaskType taskType;
    private TaskStatus status;
    private LocalDateTime createdAt;

    // 包含所属群组的摘要信息
    private WorkspaceSummaryDTO workspace;

    // 构造函数，用于从 Task 实体转换
    public UserTaskSummaryDTO(Task task) {
        this.taskId = task.getTaskId();
        this.title = task.getTitle();
        this.taskType = task.getTaskType();
        this.status = task.getStatus();
        this.createdAt = task.getCreatedAt();

        if (task.getWorkspace() != null) {
            this.workspace = new WorkspaceSummaryDTO(task.getWorkspace());
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public WorkspaceSummaryDTO getWorkspace() {
        return workspace;
    }

    public void setWorkspace(WorkspaceSummaryDTO workspace) {
        this.workspace = workspace;
    }
}