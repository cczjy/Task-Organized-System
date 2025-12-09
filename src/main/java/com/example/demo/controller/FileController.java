package com.example.demo.controller;

import com.example.demo.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
@RequestMapping("/api/files")
public class FileController {

    @Autowired
    private TaskService taskService; // TaskService 中有文件处理逻辑

    @GetMapping("/download/{fileId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long fileId, HttpServletRequest request) {
        // 1. 调用 Service 加载文件为 Resource
        Resource resource = taskService.loadFileAsResource(fileId);

        // 2. 尝试确定文件的 MIME 类型
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            // log error
        }

        // 3. 如果无法确定类型，则设置为通用的二进制流
        if(contentType == null) {
            contentType = "application/octet-stream";
        }

        // 4. 构建响应
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                // Content-Disposition 头让浏览器弹出下载对话框，而不是直接在页面上显示
                // attachment; filename="..." 是标准写法
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}