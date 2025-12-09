package com.example.demo.dto;

import com.example.demo.model.Membership;
import com.example.demo.model.User;
import com.example.demo.model.enums.MembershipRole;

public class WorkspaceMemberDTO {

    private Long userId;
    private String username;
    private String email;
    private String introduction;
    private MembershipRole role; // 新增：成员在该群组中的角色

    // 构造函数，用于从 Membership 实体转换
    // 传入 Membership 实体是最佳选择，因为它同时包含了 User 和 Role 的信息
    public WorkspaceMemberDTO(Membership membership) {
        User user = membership.getUser();
        this.userId = user.getUserId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.introduction = user.getIntroduction();
        this.role = membership.getRole(); // 直接从 membership 获取角色
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

    public MembershipRole getRole() {
        return role;
    }

    public void setRole(MembershipRole role) {
        this.role = role;
    }
}