package com.example.demo.dto;

import com.example.demo.model.DocumentFile;

public class DocumentSubmissionDTO {

    private UserSummaryDTO uploader;
    private DocumentFileDTO submittedFile; // 复用已有的 DocumentFileDTO

    public DocumentSubmissionDTO(DocumentFile documentFile) {
        this.uploader = new UserSummaryDTO(
                documentFile.getUploader().getUserId(),
                documentFile.getUploader().getUsername(),
                documentFile.getUploader().getEmail(),
                documentFile.getUploader().getIntroduction()
        );
        this.submittedFile = new DocumentFileDTO(documentFile);
    }

    // --- Getters and Setters ---
    public UserSummaryDTO getUploader() { return uploader; }
    public void setUploader(UserSummaryDTO uploader) { this.uploader = uploader; }
    public DocumentFileDTO getSubmittedFile() { return submittedFile; }
    public void setSubmittedFile(DocumentFileDTO submittedFile) { this.submittedFile = submittedFile; }
}