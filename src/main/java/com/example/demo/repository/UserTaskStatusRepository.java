package com.example.demo.repository;

import com.example.demo.model.UserTaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.List; // <-- 必需的 Import
import java.util.Optional;

@Repository
public interface UserTaskStatusRepository extends JpaRepository<UserTaskStatus, Long> {

    /**
     * 根据用户ID和任务ID查找唯一的任务状态记录
     */
    Optional<UserTaskStatus> findByUser_UserIdAndTask_TaskId(Long userId, Long taskId);

    /**
     * 查询指定用户在指定工作空间内的所有任务状态记录
     * @param userId 用户的ID
     * @param workspaceId 工作空间的ID
     * @return 该用户在该工作空间内的所有任务状态列表
     */
    @Query("SELECT s FROM UserTaskStatus s WHERE s.user.userId = :userId AND s.task.workspace.workspaceId = :workspaceId")
    List<UserTaskStatus> findUserStatusInWorkspace(@Param("userId") Long userId, @Param("workspaceId") Long workspaceId);

    /**
     * 根据任务ID列表，批量删除所有相关的用户任务状态记录
     * @param taskIds 需要删除状态的任务ID列表
     */
    @Modifying // 告诉 Spring Data JPA 这是一个会修改数据库的操作 (DELETE/UPDATE)
    @Query("DELETE FROM UserTaskStatus uts WHERE uts.task.taskId IN :taskIds")
    void deleteAllByTask_TaskIdIn(@Param("taskIds") List<Long> taskIds);
}