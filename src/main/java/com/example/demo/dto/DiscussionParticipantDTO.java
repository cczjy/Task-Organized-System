package com.example.demo.dto;

import com.example.demo.model.User;

public class DiscussionParticipantDTO {

    private UserSummaryDTO participant;
    private long commentCount;

    public DiscussionParticipantDTO(User user, long commentCount) {
        this.participant = new UserSummaryDTO(
                user.getUserId(),
                user.getUsername(),
                user.getEmail(),
                user.getIntroduction()
        );
        this.commentCount = commentCount;
    }

    // --- Getters and Setters ---
    public UserSummaryDTO getParticipant() { return participant; }
    public void setParticipant(UserSummaryDTO participant) { this.participant = participant; }
    public long getCommentCount() { return commentCount; }
    public void setCommentCount(long commentCount) { this.commentCount = commentCount; }
}