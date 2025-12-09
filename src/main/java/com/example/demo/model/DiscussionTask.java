package com.example.demo.model;

import com.example.demo.model.enums.TaskType;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "task_discussion")
public class DiscussionTask extends Task {

    @OneToMany(mappedBy = "discussionTask", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("createdAt ASC") // 确保顶层评论按时间排序
    private List<Comment> comments = new ArrayList<>();

    public DiscussionTask() {
        super(TaskType.DISCUSSION);
    }

    // --- Getters and Setters ---

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }
}