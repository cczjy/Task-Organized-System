package com.example.demo.service;

import com.example.demo.model.Invitation;
import com.example.demo.model.Membership;
import com.example.demo.model.User;
import com.example.demo.model.enums.InvitationStatus;
import com.example.demo.model.enums.MembershipRole;
import com.example.demo.repository.InvitationRepository;
import com.example.demo.repository.MembershipRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.demo.dto.InvitationDTO;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class InvitationService {

    @Autowired
    private InvitationRepository invitationRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MembershipRepository membershipRepository;

    /**
     * 用户接受邀请
     * @param invitationId 邀请ID
     * @param inviteeId    接受邀请的用户ID
     * @return 如果成功返回 true, 否则返回 false
     */
    @Transactional
    public boolean acceptInvitation(Long invitationId, Long inviteeId) {
        Invitation invitation = invitationRepository.findById(invitationId).orElse(null);
        User invitee = userRepository.findById(inviteeId).orElse(null);

        // 1. 校验：邀请和用户必须存在，且邀请是发给该用户的，且状态是 PENDING
        if (invitation == null || invitee == null ||
                !invitation.getInviteeEmail().equals(invitee.getEmail()) ||
                invitation.getStatus() != InvitationStatus.PENDING) {
            return false;
        }

        // 2. 更新邀请状态
        invitation.setStatus(InvitationStatus.ACCEPTED);
        invitationRepository.save(invitation);

        // 3. 创建新的成员关系 (Membership)
        Membership newMembership = new Membership();
        newMembership.setUser(invitee);
        newMembership.setWorkspace(invitation.getWorkspace());
        newMembership.setRole(MembershipRole.MEMBER); // 默认角色为 MEMBER
        membershipRepository.save(newMembership);

        return true;
    }

    /**
     * 用户拒绝邀请
     * @param invitationId 邀请ID
     * @param inviteeId    拒绝邀请的用户ID
     * @return 如果成功返回 true, 否则返回 false
     */
    public boolean declineInvitation(Long invitationId, Long inviteeId) {
        Invitation invitation = invitationRepository.findById(invitationId).orElse(null);
        User invitee = userRepository.findById(inviteeId).orElse(null);

        // 校验逻辑同上
        if (invitation == null || invitee == null ||
                !invitation.getInviteeEmail().equals(invitee.getEmail()) ||
                invitation.getStatus() != InvitationStatus.PENDING) {
            return false;
        }

        // 仅更新邀请状态
        invitation.setStatus(InvitationStatus.DECLINED);
        invitationRepository.save(invitation);

        return true;
    }

    /**
     * 根据用户的邮箱获取所有待处理(PENDING)的邀请
     * @param userEmail 当前用户的邮箱
     * @return 包含邀请详情的 DTO 列表
     */
    public List<InvitationDTO> getPendingInvitationsForUser(String userEmail) {
        // 1. 从数据库中查询所有发往该邮箱且状态为 PENDING 的邀请
        List<Invitation> pendingInvitations = invitationRepository.findByInviteeEmailAndStatus(userEmail, InvitationStatus.PENDING);

        // 2. 将查询到的实体列表转换为 DTO 列表
        return pendingInvitations.stream()
                .map(InvitationDTO::new) // 等同于 .map(invitation -> new InvitationDTO(invitation))
                .collect(Collectors.toList());
    }
}