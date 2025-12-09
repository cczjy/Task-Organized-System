package com.example.demo.dto;

import com.example.demo.model.User;
import com.example.demo.model.VoteOption;

public class VoteParticipantDTO {

    private UserSummaryDTO voter;
    private String chosenOptionContent;

    public VoteParticipantDTO(User voter, VoteOption option) {
        this.voter = new UserSummaryDTO(
                voter.getUserId(),
                voter.getUsername(),
                voter.getEmail(),
                voter.getIntroduction()
        );
        this.chosenOptionContent = option.getContent();
    }

    // --- Getters and Setters ---
    public UserSummaryDTO getVoter() { return voter; }
    public void setVoter(UserSummaryDTO voter) { this.voter = voter; }
    public String getChosenOptionContent() { return chosenOptionContent; }
    public void setChosenOptionContent(String chosenOptionContent) { this.chosenOptionContent = chosenOptionContent; }
}