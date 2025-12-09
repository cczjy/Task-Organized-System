package com.example.demo.dto;

import com.example.demo.model.UserTaskStatus;
import com.example.demo.model.enums.TaskCompletionState;
import java.time.LocalDateTime;

public class UserTaskStatusDTO {

    private Long userId;
    private Long taskId;
    private TaskCompletionState status;
    private LocalDateTime updatedAt;

    public UserTaskStatusDTO(UserTaskStatus userTaskStatus) {
        this.userId = userTaskStatus.getUser().getUserId();
        this.taskId = userTaskStatus.getTask().getTaskId();
        this.status = userTaskStatus.getStatus();
        this.updatedAt = userTaskStatus.getUpdatedAt();
    }

    // --- Getters and Setters ---
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getTaskId() { return taskId; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }
    public TaskCompletionState getStatus() { return status; }
    public void setStatus(TaskCompletionState status) { this.status = status; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}