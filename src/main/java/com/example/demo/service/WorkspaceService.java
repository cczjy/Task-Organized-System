package com.example.demo.service;

import com.example.demo.model.*;
import com.example.demo.model.enums.InvitationStatus;
import com.example.demo.model.enums.MembershipRole;
import com.example.demo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.demo.dto.UserSummaryDTO;
import com.example.demo.dto.TaskSummaryDTO;
import com.example.demo.service.InvitationResult;
import com.example.demo.dto.InvitationDTO;
import com.example.demo.model.enums.InvitationStatus;
import com.example.demo.dto.WorkspaceMemberDTO;
import com.example.demo.repository.UserTaskStatusRepository;

import java.util.Optional;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Collections;
import java.util.Comparator;

@Service
public class WorkspaceService {

    @Autowired
    private WorkspaceRepository workspaceRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MembershipRepository membershipRepository;
    @Autowired
    private InvitationRepository invitationRepository;
    @Autowired
    private UserTaskStatusRepository userTaskStatusRepository;

    /**
     * 创建一个新的群组 (Workspace)
     * 创建者会自动成为该群组的 'OWNER'
     * @param name 群组名称
     * @param description 群组描述
     * @param ownerId 创建者的用户ID
     * @return 创建的 Workspace 对象，如果创建者不存在则返回 null
     */
    @Transactional
    public Workspace createWorkspace(String name, String description, Long ownerId) {
        User owner = userRepository.findById(ownerId).orElse(null);
        if (owner == null) {
            return null; // 用户不存在，无法创建
        }

        // 1. 创建并保存 Workspace
        Workspace newWorkspace = new Workspace();
        newWorkspace.setName(name);
        newWorkspace.setDescription(description);
        newWorkspace.setOwner(owner);
        workspaceRepository.save(newWorkspace);

        // 2. 创建群主与群组的成员关系 (Membership)
        Membership ownerMembership = new Membership();
        ownerMembership.setUser(owner);
        ownerMembership.setWorkspace(newWorkspace);
        ownerMembership.setRole(MembershipRole.OWNER);
        membershipRepository.save(ownerMembership);

        return newWorkspace;
    }

    /**
     * 获取指定群组的所有成员列表 (返回包含角色的DTO)
     * @param workspaceId 群组ID
     * @return 包含用户基本信息和角色的 DTO 列表
     */
    public List<WorkspaceMemberDTO> getMembers(Long workspaceId) { // 2. 修改返回类型
        return membershipRepository.findByWorkspace_WorkspaceId(workspaceId)
                .stream()
                // 3. 将每个 Membership 实体转换为 WorkspaceMemberDTO
                .map(WorkspaceMemberDTO::new) // 等同于 .map(membership -> new WorkspaceMemberDTO(membership))
                .collect(Collectors.toList());
    }

    /**
     * 邀请一个新成员加入群组
     * @return 返回一个 InvitationResult 枚举，表明邀请的结果
     */
    // 2. 修改方法签名，让它返回一个包含 InvitationResult 和 Invitation 对象的容器
    public Map<InvitationResult, Invitation> inviteMember(Long inviterId, Long workspaceId, String inviteeEmail) {
        // 1. 检查邀请人和工作空间是否存在
        Optional<User> inviterOpt = userRepository.findById(inviterId);
        if (inviterOpt.isEmpty()) {
            return Map.of(InvitationResult.INVITER_NOT_FOUND, null);
        }

        Optional<Workspace> workspaceOpt = workspaceRepository.findById(workspaceId);
        if (workspaceOpt.isEmpty()) {
            return Map.of(InvitationResult.WORKSPACE_NOT_FOUND, null);
        }

        // 2. 新增：检查被邀请的用户是否存在于系统中
        Optional<User> inviteeOpt = userRepository.findByEmail(inviteeEmail);
        if (inviteeOpt.isEmpty()) {
            return Map.of(InvitationResult.INVITEE_NOT_REGISTERED, null);
        }

        // 3. 权限检查
        boolean isAuthorized = membershipRepository.findByWorkspace_WorkspaceIdAndUser_UserId(workspaceId, inviterId)
                .map(membership -> membership.getRole() == MembershipRole.OWNER || membership.getRole() == MembershipRole.ADMIN) // <-- 现在 OWNER 或 ADMIN 都可以
                .orElse(false);
        if (!isAuthorized) {
            return Map.of(InvitationResult.NO_PERMISSION, null);
        }

        // 4. 检查被邀请者是否已经是成员
        boolean isAlreadyMember = membershipRepository.findByWorkspace_WorkspaceId(workspaceId)
                .stream()
                .anyMatch(membership -> membership.getUser().getEmail().equals(inviteeEmail));
        if (isAlreadyMember) {
            return Map.of(InvitationResult.INVITEE_ALREADY_MEMBER, null);
        }

        // 5. 创建并保存邀请
        Invitation newInvitation = new Invitation();
        newInvitation.setInviter(inviterOpt.get());
        newInvitation.setWorkspace(workspaceOpt.get());
        newInvitation.setInviteeEmail(inviteeEmail);
        newInvitation.setStatus(InvitationStatus.PENDING);

        try {
            Invitation savedInvitation = invitationRepository.save(newInvitation);
            return Map.of(InvitationResult.SUCCESS, savedInvitation);
        } catch (Exception e) {
            return Map.of(InvitationResult.UNKNOWN_ERROR, null);
        }
    }

    /**
     * 获取指定群组的所有任务列表 (返回DTO)
     * @param workspaceId 群组ID
     * @return 包含任务摘要信息的 DTO 列表
     */
    @Transactional(readOnly = true) // 这是一个只读操作，添加此注解可以优化性能
    public List<TaskSummaryDTO> getTasksForWorkspace(Long workspaceId) {
        Workspace workspace = workspaceRepository.findById(workspaceId).orElse(null);
        if (workspace == null) {
            // 如果工作空间不存在，返回一个空列表
            return Collections.emptyList();
        }

        // workspace.getTasks() 默认是懒加载，需要确保在事务中访问
        // 因为有 @Transactional 注解，所以这里是安全的
        return workspace.getTasks().stream()
                // 可以根据创建时间倒序排列，让最新的任务显示在最前面
                .sorted(Comparator.comparing(Task::getCreatedAt).reversed())
                // 将每个 Task 实体转换为 TaskSummaryDTO
                .map(TaskSummaryDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * 获取指定群组的所有待处理(PENDING)邀请
     * @param workspaceId 群组ID
     * @return 包含邀请详情的 DTO 列表
     */
    public List<InvitationDTO> getPendingInvitationsForWorkspace(Long workspaceId) {
        // 1. 调用 Repository 中的新方法来获取数据
        List<Invitation> pendingInvitations = invitationRepository.findByWorkspace_WorkspaceIdAndStatus(
                workspaceId,
                InvitationStatus.PENDING
        );

        // 2. 将查询到的 Invitation 实体列表转换为 DTO 列表
        return pendingInvitations.stream()
                .map(InvitationDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * 将指定成员提升为管理员
     * @param requesterId 发起操作的用户ID (必须是群主)
     * @param workspaceId 群组ID
     * @param targetUserId 要提升的目标成员ID
     * @return 如果成功返回 true, 否则返回 false
     */
    @Transactional
    public boolean promoteToAdmin(Long requesterId, Long workspaceId, Long targetUserId) {
        // 1. 权限检查：检查发起者是否是群主
        Optional<Membership> requesterMembershipOpt = membershipRepository.findByWorkspace_WorkspaceIdAndUser_UserId(workspaceId, requesterId);
        if (requesterMembershipOpt.isEmpty() || requesterMembershipOpt.get().getRole() != MembershipRole.OWNER) {
            return false; // 发起者不是群主，没有权限
        }

        // 2. 查找目标成员的成员关系记录
        Optional<Membership> targetMembershipOpt = membershipRepository.findByWorkspace_WorkspaceIdAndUser_UserId(workspaceId, targetUserId);
        if (targetMembershipOpt.isEmpty() || targetMembershipOpt.get().getRole() != MembershipRole.MEMBER) {
            // 目标用户不是该群组的普通成员（可能已经是Admin/Owner，或者根本不是成员）
            return false;
        }

        // 3. 更新角色并保存
        Membership targetMembership = targetMembershipOpt.get();
        targetMembership.setRole(MembershipRole.ADMIN);
        membershipRepository.save(targetMembership);

        return true;
    }

    /**
     * 将指定管理员降级为普通成员
     * @param requesterId 发起操作的用户ID (必须是群主)
     * @param workspaceId 群组ID
     * @param targetUserId 要降级的目标管理员ID
     * @return 如果成功返回 true, 否则返回 false
     */
    @Transactional
    public boolean demoteToMember(Long requesterId, Long workspaceId, Long targetUserId) {
        // 1. 权限检查：检查发起者是否是群主
        Optional<Membership> requesterMembershipOpt = membershipRepository.findByWorkspace_WorkspaceIdAndUser_UserId(workspaceId, requesterId);
        if (requesterMembershipOpt.isEmpty() || requesterMembershipOpt.get().getRole() != MembershipRole.OWNER) {
            return false; // 发起者不是群主，没有权限
        }

        // 2. 查找目标管理员的成员关系记录
        Optional<Membership> targetMembershipOpt = membershipRepository.findByWorkspace_WorkspaceIdAndUser_UserId(workspaceId, targetUserId);
        if (targetMembershipOpt.isEmpty() || targetMembershipOpt.get().getRole() != MembershipRole.ADMIN) {
            // 目标用户不是该群组的管理员
            return false;
        }

        // 3. 更新角色并保存
        Membership targetMembership = targetMembershipOpt.get();
        targetMembership.setRole(MembershipRole.MEMBER);
        membershipRepository.save(targetMembership);

        return true;
    }

    /**
     * 从群组中移除一个成员 (仅群主可操作)
     * @param requesterId  发起操作的用户ID (必须是群主)
     * @param workspaceId  群组ID
     * @param targetUserId 要被移除的目标成员ID
     * @return 如果成功返回 true, 否则返回 false
     */
    @Transactional
    public boolean removeMember(Long requesterId, Long workspaceId, Long targetUserId) {
        // 1. 权限检查：检查发起者是否是群主 (使用正确的方法名)
        Optional<Membership> requesterMembershipOpt = membershipRepository.findByWorkspace_WorkspaceIdAndUser_UserId(workspaceId, requesterId);
        if (requesterMembershipOpt.isEmpty() || requesterMembershipOpt.get().getRole() != MembershipRole.OWNER) {
            return false; // 发起者不是群主，没有权限
        }

        // 2. 安全检查：群主不能移除自己
        if (requesterId.equals(targetUserId)) {
            return false; // 群主不能移除自己
        }

        // 3. 查找目标成员的成员关系记录 (使用正确的方法名)
        Optional<Membership> targetMembershipOpt = membershipRepository.findByWorkspace_WorkspaceIdAndUser_UserId(workspaceId, targetUserId);
        if (targetMembershipOpt.isEmpty()) {
            return false; // 目标用户不是该群组的成员
        }

        // 4. 删除成员关系记录
        membershipRepository.delete(targetMembershipOpt.get());

        return true;
    }

    /**
     * 解散并删除一个群组 (仅群主可操作)
     * @param requesterId 发起操作的用户ID (必须是群主)
     * @param workspaceId 要解散的群组ID
     * @return 如果成功返回 true, 否则返回 false
     */
    @Transactional
    public boolean deleteWorkspace(Long requesterId, Long workspaceId) {
        // 1. 查找群组
        Optional<Workspace> workspaceOpt = workspaceRepository.findById(workspaceId);
        if (workspaceOpt.isEmpty()) {
            return false; // 群组不存在
        }
        Workspace workspace = workspaceOpt.get();

        // 2. 权限检查：确认发起者是该群组的群主
        if (!workspace.getOwner().getUserId().equals(requesterId)) {
            return false; // 发起者不是群主，没有权限
        }

        // --- 开始清理所有依赖项 ---

        // 3. 获取群组内所有任务的ID
        List<Long> taskIds = workspace.getTasks().stream()
                .map(Task::getTaskId)
                .collect(Collectors.toList());

        // 4. (关键步骤) 如果存在任务，则先删除所有与这些任务关联的 UserTaskStatus 记录
        if (!taskIds.isEmpty()) {
            userTaskStatusRepository.deleteAllByTask_TaskIdIn(taskIds);
        }

        // 5. 删除所有与该群组相关的 Invitation 记录
        List<Invitation> invitations = invitationRepository.findByWorkspace_WorkspaceId(workspaceId);
        if (!invitations.isEmpty()) {
            invitationRepository.deleteAll(invitations);
        }

        // 6. 最后，删除群组本身。
        // 此时，因为 UserTaskStatus 已经被清理，JPA 的级联删除现在可以
        // 成功地删除 Tasks, Memberships, VoteOptions, Comments, DocumentFiles 等。
        workspaceRepository.delete(workspace);

        return true;
    }
}