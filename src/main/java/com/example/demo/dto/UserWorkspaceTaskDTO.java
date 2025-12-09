package com.example.demo.dto;

import com.example.demo.model.Task;
import com.example.demo.model.UserTaskStatus;
import com.example.demo.model.enums.TaskCompletionState;
import com.example.demo.model.enums.TaskStatus;
import com.example.demo.model.enums.TaskType;
import java.time.LocalDateTime;

public class UserWorkspaceTaskDTO {

    private Long taskId;
    private String title;
    private TaskType taskType;
    private TaskStatus overallStatus;
    private LocalDateTime createdAt;
    private TaskCompletionState userCompletionState;
    private UserSummaryDTO creator;

    // 新增字段：任务所属的群组信息
    private WorkspaceSummaryDTO workspace;

    // 构造函数
    public UserWorkspaceTaskDTO(Task task, UserTaskStatus userTaskStatus) {
        this.taskId = task.getTaskId();
        this.title = task.getTitle();
        this.taskType = task.getTaskType();
        this.overallStatus = task.getStatus();
        this.createdAt = task.getCreatedAt();

        this.userCompletionState = (userTaskStatus != null) ? userTaskStatus.getStatus() : TaskCompletionState.PENDING;

        if (task.getCreator() != null) {
            this.creator = new UserSummaryDTO(
                    task.getCreator().getUserId(),
                    task.getCreator().getUsername(),
                    task.getCreator().getEmail(),
                    task.getCreator().getIntroduction()
            );
        }

        // 新增逻辑：填充群组信息
        if (task.getWorkspace() != null) {
            // 这里我们不能直接使用 new WorkspaceSummaryDTO(membership)，
            // 因为我们没有 membership 对象。
            // 我们需要创建一个可以接收 Workspace 对象的构造函数，或者直接在这里创建。
            this.workspace = new WorkspaceSummaryDTO(task.getWorkspace());
        }
    }

    // --- Getters and Setters ---

    public Long getTaskId() { return taskId; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public TaskType getTaskType() { return taskType; }
    public void setTaskType(TaskType taskType) { this.taskType = taskType; }
    public TaskStatus getOverallStatus() { return overallStatus; }
    public void setOverallStatus(TaskStatus overallStatus) { this.overallStatus = overallStatus; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public TaskCompletionState getUserCompletionState() { return userCompletionState; }
    public void setUserCompletionState(TaskCompletionState userCompletionState) { this.userCompletionState = userCompletionState; }
    public UserSummaryDTO getCreator() { return creator; }
    public void setCreator(UserSummaryDTO creator) { this.creator = creator; }

    // 新增 Getter and Setter
    public WorkspaceSummaryDTO getWorkspace() { return workspace; }
    public void setWorkspace(WorkspaceSummaryDTO workspace) { this.workspace = workspace; }
}