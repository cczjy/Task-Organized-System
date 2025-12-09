package com.example.demo.controller;

import com.example.demo.model.Invitation;
import com.example.demo.model.User;
import com.example.demo.model.Workspace;
import com.example.demo.service.WorkspaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.demo.dto.UserSummaryDTO;
import com.example.demo.dto.TaskSummaryDTO;
import com.example.demo.service.InvitationResult;
import com.example.demo.dto.InvitationDTO;
import com.example.demo.dto.WorkspaceMemberDTO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/workspaces")
public class WorkspaceController {

    @Autowired
    private WorkspaceService workspaceService;

    /**
     * API: 创建一个新的群组
     */
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createWorkspace(
            @RequestParam String name,
            @RequestParam(required = false) String description,
            @RequestParam Long ownerId) { // 注意：ownerId 目前需要前端传递

        Workspace workspace = workspaceService.createWorkspace(name, description, ownerId);
        Map<String, Object> response = new HashMap<>();

        if (workspace != null) {
            response.put("message", "群组创建成功");
            response.put("workspaceId", workspace.getWorkspaceId());
            response.put("name", workspace.getName());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            response.put("error", "创建失败：指定的所有者用户不存在");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * API: 获取指定群组的所有成员
     */
    @GetMapping("/{workspaceId}/members")
    public ResponseEntity<List<WorkspaceMemberDTO>> getWorkspaceMembers(@PathVariable Long workspaceId) {
        List<WorkspaceMemberDTO> members = workspaceService.getMembers(workspaceId);
        return ResponseEntity.ok(members);
    }

    /**
     * API: 邀请新成员
     */
    @PostMapping("/{workspaceId}/invitations")
    public ResponseEntity<Map<String, Object>> inviteMember(
            @PathVariable Long workspaceId,
            @RequestParam Long inviterId,
            @RequestParam String inviteeEmail) {

        Map<InvitationResult, Invitation> result = workspaceService.inviteMember(inviterId, workspaceId, inviteeEmail);
        // 获取 Map 中的第一个（也是唯一一个）条目
        Map.Entry<InvitationResult, Invitation> entry = result.entrySet().iterator().next();
        InvitationResult status = entry.getKey();
        Invitation invitation = entry.getValue();

        Map<String, Object> response = new HashMap<>();

        switch (status) {
            case SUCCESS:
                response.put("message", "邀请已发送");
                response.put("invitationId", invitation.getInvitationId());
                return ResponseEntity.ok(response);

            case INVITEE_NOT_REGISTERED:
                response.put("error", "邀请失败：被邀请的用户尚未在本系统注册");
                // 404 Not Found 也是一个合理的选择，表示找不到目标用户
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);

            case INVITEE_ALREADY_MEMBER:
                response.put("error", "邀请失败：该用户已经是群组成员");
                // 409 Conflict 表示请求与当前资源状态冲突
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);

            case NO_PERMISSION:
                response.put("error", "邀请失败：您没有权限邀请成员");
                // 403 Forbidden 表示服务器理解请求，但拒绝执行
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);

            case INVITER_NOT_FOUND:
            case WORKSPACE_NOT_FOUND:
                response.put("error", "邀请失败：邀请人或群组不存在");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

            default:
                response.put("error", "邀请失败：发生未知错误");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * API: 获取指定群组的所有任务列表
     * @param workspaceId 群组的ID
     */
    @GetMapping("/{workspaceId}/tasks")
    public ResponseEntity<List<TaskSummaryDTO>> getTasksInWorkspace(@PathVariable Long workspaceId) {
        List<TaskSummaryDTO> tasks = workspaceService.getTasksForWorkspace(workspaceId);
        return ResponseEntity.ok(tasks);
    }

    /**
     * API: 获取指定群组的所有待处理邀请
     * @param workspaceId 群组的ID
     */
    @GetMapping("/{workspaceId}/invitations/pending")
    public ResponseEntity<List<InvitationDTO>> getPendingInvitations(@PathVariable Long workspaceId) {
        List<InvitationDTO> invitations = workspaceService.getPendingInvitationsForWorkspace(workspaceId);
        return ResponseEntity.ok(invitations);
    }

    /**
     * API: 提升成员为管理员 (仅群主可操作)
     */
    @PostMapping("/{workspaceId}/members/{targetUserId}/promote")
    public ResponseEntity<Map<String, String>> promoteMemberToAdmin(
            @PathVariable Long workspaceId,
            @PathVariable Long targetUserId,
            @RequestParam Long requesterId) { // requesterId 是操作发起者，未来从Token获取

        boolean success = workspaceService.promoteToAdmin(requesterId, workspaceId, targetUserId);
        if (success) {
            return ResponseEntity.ok(Map.of("message", "用户已成功提升为管理员"));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "操作失败：您没有权限或目标用户不是普通成员"));
        }
    }

    /**
     * API: 将管理员降级为成员 (仅群主可操作)
     */
    @PostMapping("/{workspaceId}/members/{targetUserId}/demote")
    public ResponseEntity<Map<String, String>> demoteAdminToMember(
            @PathVariable Long workspaceId,
            @PathVariable Long targetUserId,
            @RequestParam Long requesterId) { // requesterId 是操作发起者，未来从Token获取

        boolean success = workspaceService.demoteToMember(requesterId, workspaceId, targetUserId);
        if (success) {
            return ResponseEntity.ok(Map.of("message", "管理员已成功降级为成员"));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "操作失败：您没有权限或目标用户不是管理员"));
        }
    }

    /**
     * API: 从群组中移除指定成员 (仅群主可操作)
     */
    @DeleteMapping("/{workspaceId}/members/{targetUserId}")
    public ResponseEntity<Map<String, String>> removeMember(
            @PathVariable Long workspaceId,
            @PathVariable Long targetUserId,
            @RequestParam Long requesterId) { // requesterId 是操作发起者，未来从Token获取

        boolean success = workspaceService.removeMember(requesterId, workspaceId, targetUserId);
        if (success) {
            return ResponseEntity.ok(Map.of("message", "成员已成功移除"));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "操作失败：您没有权限或目标用户不存在"));
        }
    }

    /**
     * API: 解散并删除群组 (仅群主可操作)
     */
    @DeleteMapping("/{workspaceId}")
    public ResponseEntity<Map<String, String>> deleteWorkspace(
            @PathVariable Long workspaceId,
            @RequestParam Long requesterId) { // requesterId 是操作发起者，未来从Token获取

        boolean success = workspaceService.deleteWorkspace(requesterId, workspaceId);
        if (success) {
            return ResponseEntity.ok(Map.of("message", "群组已成功解散"));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "操作失败：您没有权限或群组不存在"));
        }
    }
}