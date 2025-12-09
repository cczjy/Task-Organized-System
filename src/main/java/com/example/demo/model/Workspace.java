package com.example.demo.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Workspace 实体类
 * 代表一个群组或协作空间，是任务和成员的容器。
 */
@Entity
@Table(name = "workspaces")
public class Workspace {

    /**
     * 群组的唯一标识符 (主键)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long workspaceId;

    /**
     * 群组的名称
     */
    @Column(nullable = false, length = 100)
    private String name;

    /**
     * (可选) 群组的描述
     */
    @Column(length = 500)
    private String description;

    /**
     * 群组的建立时间戳
     * 使用 @CreationTimestamp 注解，在实体首次被持久化时自动设置为当前时间。
     */
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createTime;

    /**
     * 群主的 User 对象 (多对一关系)
     * 一个用户可以拥有多个群组。
     * FetchType.LAZY 是性能优化的最佳实践，避免不必要的数据库查询。
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    /**
     * 群组里面包含的任务列表 (一对多关系)
     * 一个群组可以包含多个任务。
     * `mappedBy = "workspace"` 指出这个关系的维护由 Task 实体中的 `workspace` 字段负责。
     * `cascade = CascadeType.ALL` 和 `orphanRemoval = true` 保证了任务的生命周期与群组同步。
     */
    @OneToMany(mappedBy = "workspace", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> tasks = new ArrayList<>();

    /**
     * 群组的用户列表，通过 Membership 关系表实现 (一对多关系)
     * 一个群组可以拥有多个成员关系记录。
     * 这是实现 'memList<User>' 的正确方式，因为它允许我们存储每个成员的角色信息。
     */
    @OneToMany(mappedBy = "workspace", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Membership> members = new ArrayList<>();

    // --- Getters and Setters ---
    // JPA 和其他框架需要这些方法来访问和设置类的属性。

    public Long getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(Long workspaceId) {
        this.workspaceId = workspaceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public List<Membership> getMembers() {
        return members;
    }

    public void setMembers(List<Membership> members) {
        this.members = members;
    }
}