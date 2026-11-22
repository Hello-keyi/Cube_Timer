package com.example.backend.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {

    /**
     * 上传文件，返回可访问的相对 URL（如 /uploads/xxx.png）
     */
    String upload(MultipartFile file);
}