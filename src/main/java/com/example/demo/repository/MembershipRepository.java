package com.example.demo.repository;

import com.example.demo.model.Membership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MembershipRepository extends JpaRepository<Membership, Long> {

    /**
     * 根据工作空间ID查找所有成员关系记录。
     * Spring Data JPA会解析这个方法名，并生成如下的JPQL查询：
     * "select m from Membership m where m.workspace.workspaceId = :workspaceId"
     * @param workspaceId 目标工作空间的ID
     * @return 成员关系列表
     */
    List<Membership> findByWorkspace_WorkspaceId(Long workspaceId);

    /**
     * 根据工作空间ID和用户ID查找唯一的成员关系记录。
     * Spring Data JPA会解析这个方法名，并生成如下的JPQL查询：
     * "select m from Membership m where m.workspace.workspaceId = :workspaceId and m.user.userId = :userId"
     * @param workspaceId 目标工作空间的ID
     * @param userId 目标用户的ID
     * @return 包含成员关系记录的Optional，如果不存在则为空
     */
    Optional<Membership> findByWorkspace_WorkspaceIdAndUser_UserId(Long workspaceId, Long userId);
}