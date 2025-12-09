package com.example.demo.model;

import com.example.demo.model.enums.TaskType;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "task_document")
public class DocumentTask extends Task {

    // 群主上传的说明文档
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "instruction_file_id", referencedColumnName = "fileId")
    private DocumentFile instructionFile;

    // 组员提交的文档回复
    @OneToMany(mappedBy = "documentTask", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DocumentFile> submissions = new ArrayList<>();

    public DocumentTask() {
        super(TaskType.DOCUMENT);
    }

    // --- Getters and Setters ---

    public DocumentFile getInstructionFile() {
        return instructionFile;
    }

    public void setInstructionFile(DocumentFile instructionFile) {
        this.instructionFile = instructionFile;
    }

    public List<DocumentFile> getSubmissions() {
        return submissions;
    }

    public void setSubmissions(List<DocumentFile> submissions) {
        this.submissions = submissions;
    }
}