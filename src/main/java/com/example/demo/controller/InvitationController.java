package com.example.demo.controller;

import com.example.demo.service.InvitationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.demo.dto.InvitationDTO;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/invitations")
public class InvitationController {

    @Autowired
    private InvitationService invitationService;

    /**
     * API: 接受邀请
     * @param invitationId 邀请的ID
     * @param inviteeId 接受者的用户ID (未来从Token中获取)
     */
    @PostMapping("/{invitationId}/accept")
    public ResponseEntity<Map<String, String>> acceptInvitation(
            @PathVariable Long invitationId,
            @RequestParam Long inviteeId) {

        boolean success = invitationService.acceptInvitation(invitationId, inviteeId);
        if (success) {
            return ResponseEntity.ok(Map.of("message", "已成功接受邀请并加入群组"));
        } else {
            return ResponseEntity.badRequest().body(Map.of("error", "接受邀请失败：邀请无效或您不是被邀请人"));
        }
    }

    /**
     * API: 拒绝邀请
     * @param invitationId 邀请的ID
     * @param inviteeId 拒绝者的用户ID (未来从Token中获取)
     */
    @PostMapping("/{invitationId}/decline")
    public ResponseEntity<Map<String, String>> declineInvitation(
            @PathVariable Long invitationId,
            @RequestParam Long inviteeId) {

        boolean success = invitationService.declineInvitation(invitationId, inviteeId);
        if (success) {
            return ResponseEntity.ok(Map.of("message", "已成功拒绝邀请"));
        } else {
            return ResponseEntity.badRequest().body(Map.of("error", "拒绝邀请失败：邀请无效或您不是被邀请人"));
        }
    }

    /**
     * API: 获取当前用户的所有待处理邀请
     * @param email 当前用户的邮箱 (未来可以从认证信息中获取)
     */
    @GetMapping("/pending")
    public ResponseEntity<List<InvitationDTO>> getPendingInvitations(@RequestParam String email) {
        List<InvitationDTO> invitations = invitationService.getPendingInvitationsForUser(email);
        return ResponseEntity.ok(invitations);
    }
}