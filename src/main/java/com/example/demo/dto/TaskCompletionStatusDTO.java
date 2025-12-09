package com.example.demo.dto;

import com.example.demo.model.enums.TaskType;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY) // 如果列表为空，则不包含在JSON中
public class TaskCompletionStatusDTO {

    private Long taskId;
    private String title;
    private TaskType taskType;
    private int totalMemberCount;       // 群组总人数
    private int completedMemberCount;   // 已完成/参与人数
    private double completionRate;      // 完成率

    // --- 特定类型字段 ---
    private List<DocumentSubmissionDTO> submissions;
    private List<VoteParticipantDTO> votes;
    private List<DiscussionParticipantDTO> participants;

    // --- Getters and Setters ---
    public Long getTaskId() { return taskId; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public TaskType getTaskType() { return taskType; }
    public void setTaskType(TaskType taskType) { this.taskType = taskType; }
    public int getTotalMemberCount() { return totalMemberCount; }
    public void setTotalMemberCount(int totalMemberCount) { this.totalMemberCount = totalMemberCount; }
    public int getCompletedMemberCount() { return completedMemberCount; }
    public void setCompletedMemberCount(int completedMemberCount) { this.completedMemberCount = completedMemberCount; }
    public double getCompletionRate() { return completionRate; }
    public void setCompletionRate(double completionRate) { this.completionRate = completionRate; }
    public List<DocumentSubmissionDTO> getSubmissions() { return submissions; }
    public void setSubmissions(List<DocumentSubmissionDTO> submissions) { this.submissions = submissions; }
    public List<VoteParticipantDTO> getVotes() { return votes; }
    public void setVotes(List<VoteParticipantDTO> votes) { this.votes = votes; }
    public List<DiscussionParticipantDTO> getParticipants() { return participants; }
    public void setParticipants(List<DiscussionParticipantDTO> participants) { this.participants = participants; }
}