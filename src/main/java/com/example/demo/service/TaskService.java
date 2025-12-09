package com.example.demo.service;

import com.example.demo.dto.TaskDetailDTO;
import com.example.demo.model.*;
import com.example.demo.model.enums.TaskStatus;
import com.example.demo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.example.demo.dto.UserTaskSummaryDTO;
import org.springframework.data.domain.PageRequest;
import com.example.demo.model.enums.TaskStatus;
import java.net.MalformedURLException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import com.example.demo.dto.CommentDTO;
import com.example.demo.model.enums.MembershipRole;
import com.example.demo.dto.*;
import com.example.demo.model.UserTaskStatus;
import com.example.demo.model.enums.TaskCompletionState;
import com.example.demo.dto.UserTaskStatusDTO;
import com.example.demo.dto.UserWorkspaceTaskDTO;
import java.util.Comparator;

import java.util.Optional;
import java.util.Map;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class TaskService {

    // 文件存储的根目录，建议放在项目外部或使用配置注入
    private final Path fileStorageLocation;

    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private WorkspaceRepository workspaceRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DocumentFileRepository documentFileRepository;
    @Autowired
    private VoteOptionRepository voteOptionRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private MembershipRepository membershipRepository;
    @Autowired
    private UserTaskStatusRepository userTaskStatusRepository; // 2. 注入新 Repository

    public TaskService() {
        // 初始化文件存储目录
        this.fileStorageLocation = Paths.get("uploads").toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    // --- 对外公开的 DTO 获取方法 ---
    @Transactional(readOnly = true)
    public TaskDetailDTO getTaskDetailsById(Long taskId) {
        Task task = this.getTaskById(taskId);
        if (task == null) {
            return null;
        }
        return new TaskDetailDTO(task);
    }

    // --- 对内使用的实体获取方法 ---
    private Task getTaskById(Long taskId) {
        return taskRepository.findById(taskId).orElse(null);
    }

    // --- 私有权限检查辅助方法 ---
    private boolean canCreateTask(Long userId, Long workspaceId) {
        return membershipRepository.findByWorkspace_WorkspaceIdAndUser_UserId(workspaceId, userId)
                .map(membership -> membership.getRole() == MembershipRole.OWNER || membership.getRole() == MembershipRole.ADMIN)
                .orElse(false);
    }

    // 私有辅助方法
    private void initializeTaskStatusForMembers(Task task) {
        List<User> members = membershipRepository.findByWorkspace_WorkspaceId(task.getWorkspace().getWorkspaceId())
                .stream()
                // 我们为所有角色初始化状态，也可以根据需求过滤，例如只为 MEMBER
                .map(Membership::getUser)
                .collect(Collectors.toList());

        for (User member : members) {
            UserTaskStatus status = new UserTaskStatus();
            status.setUser(member);
            status.setTask(task);
            status.setStatus(TaskCompletionState.PENDING);
            userTaskStatusRepository.save(status);
        }
    }

    // 一个私有辅助方法用于更新状态
    private void updateUserTaskStatusToCompleted(Long userId, Long taskId) {
        userTaskStatusRepository.findByUser_UserIdAndTask_TaskId(userId, taskId)
                .ifPresent(status -> {
                    status.setStatus(TaskCompletionState.COMPLETED);
                    userTaskStatusRepository.save(status);
                });
    }

    // --- 文档任务相关 ---
    @Transactional
    public DocumentTask createDocumentTask(String title, String description, Long creatorId, Long workspaceId, MultipartFile instructionFile) {
        User creator = userRepository.findById(creatorId).orElse(null);
        Workspace workspace = workspaceRepository.findById(workspaceId).orElse(null);
        if (creator == null || workspace == null) return null;

        // 1. 创建任务对象
        DocumentTask taskToSave = new DocumentTask();
        taskToSave.setTitle(title);
        taskToSave.setDescription(description);
        taskToSave.setCreator(creator);
        taskToSave.setWorkspace(workspace);
        taskToSave.setStatus(TaskStatus.TODO);

        // 2. 第一次保存以获取ID
        DocumentTask savedTask = taskRepository.save(taskToSave);

        // 3. 使用带有ID的 'savedTask' 来处理文件
        DocumentFile docFile = storeFile(instructionFile, creator, savedTask);
        savedTask.setInstructionFile(docFile);

        // 4. 初始化成员状态
        initializeTaskStatusForMembers(savedTask);

        // 5. 再次保存以更新文件关联，并返回最终结果
        return taskRepository.save(savedTask);
    }


    @Transactional
    public DocumentFile submitDocument(Long taskId, Long uploaderId, MultipartFile submissionFile) {
        Task task = getTaskById(taskId);
        User uploader = userRepository.findById(uploaderId).orElse(null);
        if (uploader == null || !(task instanceof DocumentTask)) return null;

        DocumentFile submission = storeFile(submissionFile, uploader, (DocumentTask) task);
        // 调用更新后的 storeFile 方法
        updateUserTaskStatusToCompleted(uploaderId, taskId);

        return submission;
    }

    // --- 文件下载服务 ---
    public Resource loadFileAsResource(Long fileId) {
        DocumentFile docFile = documentFileRepository.findById(fileId).orElse(null);
        if (docFile == null) {
            throw new RuntimeException("File not found with id " + fileId);
        }
        try {
            Path filePath = Paths.get(docFile.getFileUrl());
            Resource resource = new UrlResource(filePath.toUri());
            if(resource.exists()) {
                return resource;
            } else {
                throw new RuntimeException("File not found " + docFile.getFileName());
            }
        } catch (MalformedURLException ex) {
            throw new RuntimeException("File not found " + docFile.getFileName(), ex);
        }
    }

    // --- 投票任务相关 ---
    @Transactional
    public VoteTask createVoteTask(String title, String description, Long creatorId, Long workspaceId, List<String> optionContents) {
        User creator = userRepository.findById(creatorId).orElse(null);
        Workspace workspace = workspaceRepository.findById(workspaceId).orElse(null);
        if (creator == null || workspace == null) return null;

        VoteTask taskToSave = new VoteTask();
        taskToSave.setTitle(title);
        taskToSave.setDescription(description);
        taskToSave.setCreator(creator);
        taskToSave.setWorkspace(workspace);
        taskToSave.setStatus(TaskStatus.TODO);

        for (String content : optionContents) {
            VoteOption option = new VoteOption();
            option.setContent(content);
            option.setVoteTask(taskToSave);
            taskToSave.getOptions().add(option);
        }

        // 保存任务
        VoteTask savedTask = taskRepository.save(taskToSave);

        // 初始化成员状态
        initializeTaskStatusForMembers(savedTask);

        return savedTask;
    }

    @Transactional
    public boolean castVote(Long taskId, Long userId, Long optionId) {
        User user = userRepository.findById(userId).orElse(null);
        VoteOption option = voteOptionRepository.findById(optionId).orElse(null);
        Task task = getTaskById(taskId);
        if (user == null || option == null || !(task instanceof VoteTask) || !Objects.equals(option.getVoteTask().getTaskId(), taskId)) {
            return false;
        }

        option.getVoters().add(user);
        voteOptionRepository.save(option);

        updateUserTaskStatusToCompleted(userId, taskId);
        return true;
    }

    // --- 讨论任务相关 ---
    @Transactional
    public DiscussionTask createDiscussionTask(String title, String description, Long creatorId, Long workspaceId) {
        User creator = userRepository.findById(creatorId).orElse(null);
        Workspace workspace = workspaceRepository.findById(workspaceId).orElse(null);
        if (creator == null || workspace == null) return null;

        DiscussionTask taskToSave = new DiscussionTask();
        taskToSave.setTitle(title);
        taskToSave.setDescription(description);
        taskToSave.setCreator(creator);
        taskToSave.setWorkspace(workspace);
        taskToSave.setStatus(TaskStatus.TODO);

        // 保存任务
        DiscussionTask savedTask = taskRepository.save(taskToSave);

        // 初始化成员状态
        initializeTaskStatusForMembers(savedTask);

        return savedTask;
    }

    @Transactional
    public CommentDTO addComment(Long taskId, Long authorId, String content) { // 2. 修改返回类型
        User author = userRepository.findById(authorId).orElse(null);
        Task task = getTaskById(taskId);
        if (author == null || !(task instanceof DiscussionTask)) {
            return null;
        }

        Comment comment = new Comment();
        comment.setContent(content);
        comment.setAuthor(author);
        comment.setDiscussionTask((DiscussionTask) task);

        Comment savedComment = commentRepository.save(comment);

        updateUserTaskStatusToCompleted(authorId, taskId);

        // 3. 返回新创建的 CommentDTO
        return new CommentDTO(savedComment);
    }

    @Transactional
    public CommentDTO replyToComment(Long parentCommentId, Long authorId, String content) { // 4. 修改返回类型
        User author = userRepository.findById(authorId).orElse(null);
        Comment parentComment = commentRepository.findById(parentCommentId).orElse(null);
        if (author == null || parentComment == null) {
            return null;
        }

        Comment reply = new Comment();
        reply.setContent(content);
        reply.setAuthor(author);
        reply.setParentComment(parentComment);
        reply.setDiscussionTask(parentComment.getDiscussionTask());

        Comment savedReply = commentRepository.save(reply);

        updateUserTaskStatusToCompleted(authorId, parentComment.getDiscussionTask().getTaskId());

        // 5. 返回新创建的 CommentDTO
        return new CommentDTO(savedReply);
    }

    // --- 私有文件存储辅助方法 ---
    private DocumentFile storeFile(MultipartFile file, User uploader, DocumentTask task) {
        String originalFileName = org.springframework.util.StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));

        // 使用 UUID 生成一个唯一的文件名，防止重名覆盖
        String uniqueFileName = UUID.randomUUID().toString() + "_" + originalFileName;

        try {
            if(originalFileName.contains("..")) {
                throw new RuntimeException("Sorry! Filename contains invalid path sequence " + originalFileName);
            }

            // 1. 构建动态存储路径：uploads/{workspaceId}/{taskId}/
            Path taskDirectory = this.fileStorageLocation
                    .resolve(String.valueOf(task.getWorkspace().getWorkspaceId()))
                    .resolve(String.valueOf(task.getTaskId()));

            // 2. 如果目录不存在，则创建它
            Files.createDirectories(taskDirectory);

            // 3. 确定最终的文件存储位置
            Path targetLocation = taskDirectory.resolve(uniqueFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            DocumentFile docFile = new DocumentFile();
            docFile.setFileName(originalFileName); // 保存原始文件名供用户查看
            docFile.setFileUrl(targetLocation.toString()); // 保存包含唯一名的完整物理路径
            docFile.setFileSize(file.getSize());
            docFile.setFileType(file.getContentType());
            docFile.setUploader(uploader);

            if (task.getInstructionFile() == null) {
                // 如果是任务说明文件，则不设置反向关联
            } else {
                // 如果是提交的回复文件，设置反向关联
                docFile.setDocumentTask(task);
            }

            return documentFileRepository.save(docFile);
        } catch (IOException ex) {
            throw new RuntimeException("Could not store file " + originalFileName + ". Please try again!", ex);
        }
    }

    /**
     * 获取指定用户的最新的5个“个人未完成”任务
     * @param userId 用户的ID
     * @return 包含任务摘要和个人完成状态的 DTO 列表
     */
    @Transactional(readOnly = true) // <-- 2. 添加此注解
    public List<UserWorkspaceTaskDTO> getLatestUnfinishedTasksForUser(Long userId) {
        // 创建一个分页请求对象，表示我们只需要第0页（第一页）的前5条数据
        PageRequest pageRequest = PageRequest.of(0, 5);

        // 调用新的、更精确的 Repository 方法
        List<Task> tasks = taskRepository.findLatestAndPersonallyUnfinishedTasksForUser(
                userId,
                TaskCompletionState.PENDING, // 筛选条件：个人状态为 PENDING
                pageRequest
        );

        // DTO 转换逻辑保持不变
        return tasks.stream()
                .map(task -> {
                    UserTaskStatus userStatus = userTaskStatusRepository
                            .findByUser_UserIdAndTask_TaskId(userId, task.getTaskId())
                            .orElse(null);
                    return new UserWorkspaceTaskDTO(task, userStatus);
                })
                .collect(Collectors.toList());
    }

    /**
     * 获取指定任务的完成情况统计
     * @param taskId 任务ID
     * @return 包含统计信息的 DTO
     */
    @Transactional(readOnly = true)
    public TaskCompletionStatusDTO getTaskCompletionStatus(Long taskId) {
        Task task = getTaskById(taskId);
        if (task == null) return null;

        // 获取群组所有成员（不包括群主/创建者，因为他们是发布者）
        List<User> members = membershipRepository.findByWorkspace_WorkspaceId(task.getWorkspace().getWorkspaceId())
                .stream()
                .filter(m -> m.getRole() == MembershipRole.MEMBER)
                .map(Membership::getUser)
                .collect(Collectors.toList());

        int totalMemberCount = members.size();

        TaskCompletionStatusDTO statusDTO = new TaskCompletionStatusDTO();
        statusDTO.setTaskId(task.getTaskId());
        statusDTO.setTitle(task.getTitle());
        statusDTO.setTaskType(task.getTaskType());
        statusDTO.setTotalMemberCount(totalMemberCount);

        if (task instanceof DocumentTask) {
            DocumentTask docTask = (DocumentTask) task;
            List<DocumentSubmissionDTO> submissions = docTask.getSubmissions().stream()
                    .map(DocumentSubmissionDTO::new).collect(Collectors.toList());
            statusDTO.setSubmissions(submissions);
            statusDTO.setCompletedMemberCount(submissions.size());

        } else if (task instanceof VoteTask) {
            VoteTask voteTask = (VoteTask) task;
            List<VoteParticipantDTO> votes = new ArrayList<>();
            Set<Long> voters = new HashSet<>();
            for (VoteOption option : voteTask.getOptions()) {
                for (User voter : option.getVoters()) {
                    votes.add(new VoteParticipantDTO(voter, option));
                    voters.add(voter.getUserId());
                }
            }
            statusDTO.setVotes(votes);
            statusDTO.setCompletedMemberCount(voters.size());

        } else if (task instanceof DiscussionTask) {
            DiscussionTask discTask = (DiscussionTask) task;
            Map<User, Long> commentCounts = discTask.getComments().stream()
                    .collect(Collectors.groupingBy(Comment::getAuthor, Collectors.counting()));

            List<DiscussionParticipantDTO> participants = commentCounts.entrySet().stream()
                    .map(entry -> new DiscussionParticipantDTO(entry.getKey(), entry.getValue()))
                    .collect(Collectors.toList());

            statusDTO.setParticipants(participants);
            statusDTO.setCompletedMemberCount(participants.size());
        }

        if (totalMemberCount > 0) {
            statusDTO.setCompletionRate((double) statusDTO.getCompletedMemberCount() / totalMemberCount * 100);
        } else {
            statusDTO.setCompletionRate(0.0);
        }

        return statusDTO;
    }


    /**
     * 获取指定用户在特定群组中的所有任务，并附带该用户的个人完成状态
     * @param userId      用户的ID
     * @param workspaceId 群组的ID
     * @return 包含任务摘要和个人完成状态的 DTO 列表
     */
    @Transactional(readOnly = true)
    public List<UserWorkspaceTaskDTO> getTasksForUserInWorkspace(Long userId, Long workspaceId) {
        // 1. 获取该群组的所有任务
        Workspace workspace = workspaceRepository.findById(workspaceId).orElse(null);
        if (workspace == null) {
            return Collections.emptyList();
        }
        List<Task> tasksInWorkspace = workspace.getTasks();

        // 2. 遍历任务列表，为每个任务查找用户的个人状态，并创建DTO
        return tasksInWorkspace.stream()
                .sorted(Comparator.comparing(Task::getCreatedAt).reversed()) // 按创建时间倒序
                .map(task -> {
                    // 直接获取 UserTaskStatus 实体记录，可能为 null
                    UserTaskStatus userStatusRecord = userTaskStatusRepository
                            .findByUser_UserIdAndTask_TaskId(userId, task.getTaskId())
                            .orElse(null);

                    // 将完整的实体记录（或null）传递给构造函数
                    return new UserWorkspaceTaskDTO(task, userStatusRecord);
                })
                .collect(Collectors.toList());
    }
}