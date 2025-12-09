package com.example.demo.dto;

import com.example.demo.model.VoteOption;

public class VoteOptionDTO {
    private Long optionId;
    private String content;
    private int voteCount; // 只返回投票数，不暴露具体投票人列表

    public VoteOptionDTO(VoteOption option) {
        this.optionId = option.getOptionId();
        this.content = option.getContent();
        this.voteCount = option.getVoters().size();
    }
    // Getters and Setters...
    public Long getOptionId() { return optionId; }
    public void setOptionId(Long optionId) { this.optionId = optionId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public int getVoteCount() { return voteCount; }
    public void setVoteCount(int voteCount) { this.voteCount = voteCount; }
}