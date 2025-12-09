package com.example.demo.dto;

import com.example.demo.model.DocumentFile;
import java.time.LocalDateTime;

public class DocumentFileDTO {
    private Long fileId;
    private String fileName;
    private String fileType;
    private long fileSize;
    private UserSummaryDTO uploader;
    private LocalDateTime uploadedAt;
    // 注意：不暴露 fileUrl，下载将通过另一个专用API进行

    public DocumentFileDTO(DocumentFile file) {
        this.fileId = file.getFileId();
        this.fileName = file.getFileName();
        this.fileType = file.getFileType();
        this.fileSize = file.getFileSize();
        this.uploadedAt = file.getUploadedAt();
        this.uploader = new UserSummaryDTO(
                file.getUploader().getUserId(),
                file.getUploader().getUsername(),
                file.getUploader().getEmail(),
                file.getUploader().getIntroduction()
        );
    }
    // Getters and Setters...
    public Long getFileId() { return fileId; }
    public void setFileId(Long fileId) { this.fileId = fileId; }
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }
    public long getFileSize() { return fileSize; }
    public void setFileSize(long fileSize) { this.fileSize = fileSize; }
    public UserSummaryDTO getUploader() { return uploader; }
    public void setUploader(UserSummaryDTO uploader) { this.uploader = uploader; }
    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }
}