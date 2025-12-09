package com.example.demo.repository;

import com.example.demo.model.Task;
import com.example.demo.model.enums.TaskCompletionState;
import com.example.demo.model.enums.TaskStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    /**
     * (旧方法 - 保留) 查询用户所在群组中，整体状态未完成且非自己创建的任务
     */
    @Query("SELECT t FROM Task t " +
            "WHERE t.workspace.workspaceId IN " +
            "(SELECT m.workspace.workspaceId FROM Membership m WHERE m.user.userId = :userId) " +
            "AND t.status <> :excludedStatus " +
            "AND t.creator.userId <> :userId " +
            "ORDER BY t.createdAt DESC")
    List<Task> findLatestUnfinishedTasksForUser(
            @Param("userId") Long userId,
            @Param("excludedStatus") TaskStatus excludedStatus,
            Pageable pageable
    );

    /**
     * (新方法 - 核心修正) 查询用户个人状态为“待处理”的任务。
     * @param userId 用户的ID
     * @param pendingStatus 个人状态 'PENDING'
     * @param pageable 分页参数
     * @return 任务列表
     */
    @Query("SELECT t FROM Task t " +
            "LEFT JOIN UserTaskStatus uts ON t.taskId = uts.task.taskId AND uts.user.userId = :userId " +
            "WHERE t.workspace.workspaceId IN " +
            "(SELECT m.workspace.workspaceId FROM Membership m WHERE m.user.userId = :userId) " +
            "AND (uts.status IS NULL OR uts.status = :pendingStatus) " +
            "AND t.creator.userId <> :userId " +
            "ORDER BY t.createdAt DESC")
    List<Task> findLatestAndPersonallyUnfinishedTasksForUser(
            @Param("userId") Long userId,
            @Param("pendingStatus") TaskCompletionState pendingStatus,
            Pageable pageable
    );
}