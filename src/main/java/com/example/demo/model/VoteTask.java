package com.example.demo.model;

import com.example.demo.model.enums.TaskType;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "task_vote")
public class VoteTask extends Task {

    @OneToMany(mappedBy = "voteTask", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VoteOption> options = new ArrayList<>();

    public VoteTask() {
        super(TaskType.VOTE);
    }

    // --- Getters and Setters ---

    public List<VoteOption> getOptions() {
        return options;
    }

    public void setOptions(List<VoteOption> options) {
        this.options = options;
    }
}