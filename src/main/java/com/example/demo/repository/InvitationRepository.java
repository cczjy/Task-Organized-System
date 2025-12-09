package com.example.demo.repository;

import com.example.demo.model.Invitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.demo.model.enums.InvitationStatus;

import java.util.List;

/**
 * InvitationRepository 接口
 * 继承 JpaRepository 来获得一套完整的标准 CRUD (Create, Read, Update, Delete) 方法。
 * 我们可以在此基础上定义自定义的查询方法。
 */
@Repository
public interface InvitationRepository extends JpaRepository<Invitation, Long> {

    /**
     * 根据被邀请人的邮箱查找所有相关的邀请。
     * 这对于实现 "我的邀请列表" 功能非常有用。
     * Spring Data JPA 会自动根据方法名生成查询。
     *
     * @param inviteeEmail 被邀请人的邮箱地址
     * @return 一个包含所有相关邀请的列表
     */
    List<Invitation> findByInviteeEmail(String inviteeEmail);

    /**
     * 根据工作空间ID查找所有相关的邀请。
     * 这可以用于查看某个群组的所有待处理邀请。
     *
     * @param workspaceId 目标工作空间的ID
     * @return 一个包含所有相关邀请的列表
     */
    List<Invitation> findByWorkspace_WorkspaceId(Long workspaceId);

    /**
     * 根据被邀请人邮箱和邀请状态查找邀请列表
     * @param inviteeEmail 被邀请人邮箱
     * @param status 邀请状态
     * @return 邀请列表
     */
    List<Invitation> findByInviteeEmailAndStatus(String inviteeEmail, InvitationStatus status);

    /**
     * 根据工作空间ID和邀请状态查找邀请列表
     * @param workspaceId 目标工作空间的ID
     * @param status 邀请状态
     * @return 邀请列表
     */
    List<Invitation> findByWorkspace_WorkspaceIdAndStatus(Long workspaceId, InvitationStatus status);

}