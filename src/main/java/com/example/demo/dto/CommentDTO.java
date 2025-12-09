package com.example.demo.dto;

import com.example.demo.model.Comment;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class CommentDTO {
    private Long commentId;
    private String content;
    private UserSummaryDTO author;
    private LocalDateTime createdAt;
    private List<CommentDTO> replies; // 嵌套的回复列表

    public CommentDTO(Comment comment) {
        this.commentId = comment.getCommentId();
        this.content = comment.getContent();
        this.author = new UserSummaryDTO(
                comment.getAuthor().getUserId(),
                comment.getAuthor().getUsername(),
                comment.getAuthor().getEmail(),
                comment.getAuthor().getIntroduction()
        );
        this.createdAt = comment.getCreatedAt();
        // 递归地转换回复，并按时间排序
        this.replies = comment.getReplies().stream()
                .sorted(Comparator.comparing(Comment::getCreatedAt))
                .map(CommentDTO::new)
                .collect(Collectors.toList());
    }
    // Getters and Setters...
    public Long getCommentId() {
        return commentId;
    }
    public void setCommentId(Long commentId) {
        this.commentId = commentId;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public UserSummaryDTO getAuthor() {
        return author;
    }
    public void setAuthor(UserSummaryDTO author) {
        this.author = author;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public List<CommentDTO> getReplies() {
        return replies;
    }
    public void setReplies(List<CommentDTO> replies) {
        this.replies = replies;
    }
}