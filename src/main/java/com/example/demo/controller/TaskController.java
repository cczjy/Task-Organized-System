package com.example.demo.controller;

import com.example.demo.model.*;
import com.example.demo.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.example.demo.dto.TaskDetailDTO;
import com.example.demo.dto.CommentDTO;
import com.example.demo.dto.TaskCompletionStatusDTO;
import com.example.demo.dto.UserTaskStatusDTO;

import java.util.Optional;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    // --- 创建任务 ---
    @PostMapping("/document")
    public ResponseEntity<?> createDocumentTask(
            @RequestParam String title, @RequestParam(required = false) String description,
            @RequestParam Long creatorId, @RequestParam Long workspaceId,
            @RequestParam("file") MultipartFile instructionFile) {

        DocumentTask task = taskService.createDocumentTask(title, description, creatorId, workspaceId, instructionFile);
        if (task == null) return ResponseEntity.badRequest().body("创建失败：用户或工作空间不存在");

        // 修改部分：返回 DTO 而不是实体
        TaskDetailDTO taskDetails = new TaskDetailDTO(task);
        return ResponseEntity.status(HttpStatus.CREATED).body(taskDetails);
    }

    @PostMapping("/vote")
    public ResponseEntity<?> createVoteTask(
            @RequestParam String title, @RequestParam(required = false) String description,
            @RequestParam Long creatorId, @RequestParam Long workspaceId,
            @RequestParam List<String> options) {

        VoteTask task = taskService.createVoteTask(title, description, creatorId, workspaceId, options);
        if (task == null) return ResponseEntity.badRequest().body("创建失败：用户或工作空间不存在");

        // 修改部分：返回 DTO 而不是实体
        TaskDetailDTO taskDetails = new TaskDetailDTO(task);
        return ResponseEntity.status(HttpStatus.CREATED).body(taskDetails);
    }

    @PostMapping("/discussion")
    public ResponseEntity<?> createDiscussionTask(
            @RequestParam String title, @RequestParam(required = false) String description,
            @RequestParam Long creatorId, @RequestParam Long workspaceId) {

        DiscussionTask task = taskService.createDiscussionTask(title, description, creatorId, workspaceId);
        if (task == null) return ResponseEntity.badRequest().body("创建失败：用户或工作空间不存在");

        // 修改部分：返回 DTO 而不是实体
        TaskDetailDTO taskDetails = new TaskDetailDTO(task);
        return ResponseEntity.status(HttpStatus.CREATED).body(taskDetails);
    }

    // --- 与任务交互 ---
    @GetMapping("/{taskId}")
    public ResponseEntity<?> getTaskDetails(@PathVariable Long taskId) { // 2. 方法名保持不变
        TaskDetailDTO taskDetails = taskService.getTaskDetailsById(taskId); // 3. 调用新的 service 方法
        if (taskDetails == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(taskDetails); // 4. 返回 DTO
    }

    @PostMapping("/{taskId}/documents/submit")
    public ResponseEntity<?> submitDocument(
            @PathVariable Long taskId, @RequestParam Long uploaderId,
            @RequestParam("file") MultipartFile submissionFile) {
        DocumentFile submission = taskService.submitDocument(taskId, uploaderId, submissionFile);
        if (submission == null) return ResponseEntity.badRequest().body("提交失败：任务或用户不存在");
        return ResponseEntity.ok(submission);
    }

    @PostMapping("/{taskId}/votes/cast")
    public ResponseEntity<?> castVote(
            @PathVariable Long taskId, @RequestParam Long userId, @RequestParam Long optionId) {
        boolean success = taskService.castVote(taskId, userId, optionId);
        if (!success) return ResponseEntity.badRequest().body("投票失败：任务、用户或选项无效");
        return ResponseEntity.ok(Map.of("message", "投票成功"));
    }

    @PostMapping("/{taskId}/comments/add")
    public ResponseEntity<?> addComment(
            @PathVariable Long taskId, @RequestParam Long authorId, @RequestParam String content) {

        // 接收 DTO 类型的返回值
        CommentDTO comment = taskService.addComment(taskId, authorId, content);

        if (comment == null) {
            return ResponseEntity.badRequest().body("评论失败：任务或用户不存在");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(comment);
    }

    @PostMapping("/comments/{parentCommentId}/reply")
    public ResponseEntity<?> replyToComment(
            @PathVariable Long parentCommentId, @RequestParam Long authorId, @RequestParam String content) {

        // 接收 DTO 类型的返回值
        CommentDTO reply = taskService.replyToComment(parentCommentId, authorId, content);

        if (reply == null) {
            return ResponseEntity.badRequest().body("回复失败：父评论或用户不存在");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(reply);
    }

    /**
     * API: 获取指定任务的完成情况统计
     * @param taskId 任务的ID
     */
    @GetMapping("/{taskId}/completion-status")
    public ResponseEntity<?> getTaskCompletionStatus(@PathVariable Long taskId) {
        TaskCompletionStatusDTO status = taskService.getTaskCompletionStatus(taskId);
        if (status == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(status);
    }

}