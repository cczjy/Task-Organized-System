package com.example.demo.dto;

import com.example.demo.model.Invitation;
import com.example.demo.model.enums.InvitationStatus;

import java.time.LocalDateTime;

public class InvitationDTO {

    private Long invitationId;
    private InvitationStatus status;
    private LocalDateTime createdAt;

    // 邀请人信息 (使用已有的 UserSummaryDTO)
    private UserSummaryDTO inviter;

    // 工作空间信息 (使用已有的 WorkspaceSummaryDTO)
    private WorkspaceSummaryDTO workspace;

    // 构造函数，用于从 Invitation 实体转换
    public InvitationDTO(Invitation invitation) {
        this.invitationId = invitation.getInvitationId();
        this.status = invitation.getStatus();
        this.createdAt = invitation.getCreatedAt();

        // 创建邀请人的摘要信息
        if (invitation.getInviter() != null) {
            this.inviter = new UserSummaryDTO(
                    invitation.getInviter().getUserId(),
                    invitation.getInviter().getUsername(),
                    invitation.getInviter().getEmail(),
                    invitation.getInviter().getIntroduction()
            );
        }

        // 创建工作空间的摘要信息
        if (invitation.getWorkspace() != null) {
            this.workspace = new WorkspaceSummaryDTO(invitation.getWorkspace());
        }
    }

    // --- Getters and Setters ---

    public Long getInvitationId() {
        return invitationId;
    }

    public void setInvitationId(Long invitationId) {
        this.invitationId = invitationId;
    }

    public InvitationStatus getStatus() {
        return status;
    }

    public void setStatus(InvitationStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public UserSummaryDTO getInviter() {
        return inviter;
    }

    public void setInviter(UserSummaryDTO inviter) {
        this.inviter = inviter;
    }

    public WorkspaceSummaryDTO getWorkspace() {
        return workspace;
    }

    public void setWorkspace(WorkspaceSummaryDTO workspace) {
        this.workspace = workspace;
    }
}