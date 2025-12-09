package com.example.demo.dto;

import com.example.demo.model.Membership;
import com.example.demo.model.Workspace;
import com.example.demo.model.enums.MembershipRole;
import com.fasterxml.jackson.annotation.JsonInclude; // 新增

@JsonInclude(JsonInclude.Include.NON_NULL) // 新增：如果 role 为 null，则不返回
public class WorkspaceSummaryDTO {

    private Long workspaceId;
    private String name;
    private MembershipRole role;

    /**
     * 构造函数 1: 从 Membership 对象构造
     * (用于登录等需要展示角色的场景)
     */
    public WorkspaceSummaryDTO(Membership membership) {
        Workspace workspace = membership.getWorkspace();
        this.workspaceId = workspace.getWorkspaceId();
        this.name = workspace.getName();
        this.role = membership.getRole();
    }

    /**
     * 构造函数 2: 从 Workspace 对象构造
     * (新增，用于任务列表等不需要展示特定用户角色的场景)
     */
    public WorkspaceSummaryDTO(Workspace workspace) {
        this.workspaceId = workspace.getWorkspaceId();
        this.name = workspace.getName();
        // 在这种情况下，role 是未知的，所以为 null
        this.role = null;
    }

    // --- Getters and Setters ---
    // ... (保持不变) ...
    public Long getWorkspaceId() { return workspaceId; }
    public void setWorkspaceId(Long workspaceId) { this.workspaceId = workspaceId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public MembershipRole getRole() { return role; }
    public void setRole(MembershipRole role) { this.role = role; }
}