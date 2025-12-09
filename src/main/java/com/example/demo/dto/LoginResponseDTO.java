package com.example.demo.dto;

import com.example.demo.model.User;
import java.util.List;
import java.util.stream.Collectors;

public class LoginResponseDTO {

    private Long userId;
    private String username;
    private String email;
    private String introduction;
    private List<WorkspaceSummaryDTO> workspaces;

    // 还可以包含 JWT Token
    // private String token;

    // 构造函数，接收一个 User 实体并进行转换
    public LoginResponseDTO(User user) {
        this.userId = user.getUserId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.introduction = user.getIntroduction();

        // --- 这里是修改的部分 ---
        // 将用户的 Membership 列表直接转换为 WorkspaceSummaryDTO 列表
        // WorkspaceSummaryDTO 的新构造函数会处理好所有事情
        this.workspaces = user.getMemberships().stream()
                .map(WorkspaceSummaryDTO::new) // 等同于 .map(membership -> new WorkspaceSummaryDTO(membership))
                .collect(Collectors.toList());
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

    public List<WorkspaceSummaryDTO> getWorkspaces() {
        return workspaces;
    }

    public void setWorkspaces(List<WorkspaceSummaryDTO> workspaces) {
        this.workspaces = workspaces;
    }
}