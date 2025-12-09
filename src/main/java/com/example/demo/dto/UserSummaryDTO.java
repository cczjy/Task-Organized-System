package com.example.demo.dto;

import com.example.demo.model.User;

public class UserSummaryDTO {

    private Long userId;
    private String username;
    private String email;
    private String introduction;

    /**
     * 构造函数 1: 从分散的字段创建 (我们之前创建的)
     * 用于明确知道所有字段值的场景
     */
    public UserSummaryDTO(Long userId, String username, String email, String introduction) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.introduction = introduction;
    }

    /**
     * 构造函数 2: 从 User 实体对象创建 (这是解决当前问题的关键)
     * 用于需要将整个实体转换为DTO的场景
     */
    public UserSummaryDTO(User user) {
        this.userId = user.getUserId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.introduction = user.getIntroduction();
    }

    // --- Getters and Setters ---

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }
}