package com.example.demo.dto;

import com.example.demo.model.*;
import com.example.demo.model.enums.TaskStatus;
import com.example.demo.model.enums.TaskType;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

// 这个注解表示：如果字段值为null，则在JSON响应中不包含该字段
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaskDetailDTO {

    // --- 通用字段 ---
    private Long taskId;
    private String title;
    private String description;
    private TaskType taskType;
    private TaskStatus status;
    private LocalDateTime dueDate;
    private LocalDateTime createdAt;
    private UserSummaryDTO creator;

    // --- 特定类型字段 ---
    private List<VoteOptionDTO> options;       // 仅用于投票任务
    private DocumentFileDTO instructionFile;   // 仅用于文档任务
    private List<DocumentFileDTO> submissions; // 仅用于文档任务
    private List<CommentDTO> comments;         // 仅用于讨论任务

    // 构造函数，根据传入的 Task 实体类型进行转换
    public TaskDetailDTO(Task task) {
        // 填充通用字段
        this.taskId = task.getTaskId();
        this.title = task.getTitle();
        this.description = task.getDescription();
        this.taskType = task.getTaskType();
        this.status = task.getStatus();
        this.dueDate = task.getDueDate();
        this.createdAt = task.getCreatedAt();
        this.creator = new UserSummaryDTO(
            task.getCreator().getUserId(),
            task.getCreator().getUsername(),
            task.getCreator().getEmail(),
            task.getCreator().getIntroduction()
        );

        // 根据任务的具体类型，填充特定字段
        if (task instanceof VoteTask) {
            VoteTask voteTask = (VoteTask) task;
            this.options = voteTask.getOptions().stream()
                    .map(VoteOptionDTO::new)
                    .collect(Collectors.toList());
        } else if (task instanceof DocumentTask) {
            DocumentTask docTask = (DocumentTask) task;
            if (docTask.getInstructionFile() != null) {
                this.instructionFile = new DocumentFileDTO(docTask.getInstructionFile());
            }
            this.submissions = docTask.getSubmissions().stream()
                    .map(DocumentFileDTO::new)
                    .collect(Collectors.toList());
        } else if (task instanceof DiscussionTask) {
            DiscussionTask discTask = (DiscussionTask) task;
            // 只转换顶层评论（parentComment 为 null 的评论）
            this.comments = discTask.getComments().stream()
                    .filter(c -> c.getParentComment() == null)
                    .sorted(Comparator.comparing(Comment::getCreatedAt))
                    .map(CommentDTO::new)
                    .collect(Collectors.toList());
        }
    }
    // Getters and Setters...
    public Long getTaskId() { return taskId; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public TaskType getTaskType() { return taskType; }
    public void setTaskType(TaskType taskType) { this.taskType = taskType; }
    public TaskStatus getStatus() { return status; }
    public void setStatus(TaskStatus status) { this.status = status; }
    public LocalDateTime getDueDate() { return dueDate; }
    public void setDueDate(LocalDateTime dueDate) { this.dueDate = dueDate; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public UserSummaryDTO getCreator() { return creator; }
    public void setCreator(UserSummaryDTO creator) { this.creator = creator; }
    public List<VoteOptionDTO> getOptions() { return options; }
    public void setOptions(List<VoteOptionDTO> options) { this.options = options; }
    public DocumentFileDTO getInstructionFile() { return instructionFile; }
    public void setInstructionFile(DocumentFileDTO instructionFile) { this.instructionFile = instructionFile; }
    public List<DocumentFileDTO> getSubmissions() { return submissions; }
    public void setSubmissions(List<DocumentFileDTO> submissions) { this.submissions = submissions; }
    public List<CommentDTO> getComments() { return comments; }
    public void setComments(List<CommentDTO> comments) { this.comments = comments; }
}