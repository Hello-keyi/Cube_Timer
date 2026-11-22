package com.example.backend.service.impl;

import com.example.backend.exception.BusinessException;
import com.example.backend.service.FileService;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;
import java.util.UUID;

@Service
@Slf4j
public class FileServiceImpl implements FileService {

    private static final Set<String> ALLOWED_TYPES =
            Set.of("image/jpeg", "image/png", "image/gif", "image/webp");

    private static final Set<String> ALLOWED_EXT =
            Set.of(".jpg", ".jpeg", ".png", ".gif", ".webp");

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    public String upload(MultipartFile file)  {

        if ( file.isEmpty()) {
            log.warn("File is empty");
            throw new BusinessException("文件不能为空");
        }


        String originalName = file.getOriginalFilename();
        if (originalName == null || !originalName.contains(".")) {
            throw new BusinessException("文件格式错误");
        }
        String ext = originalName.substring(originalName.lastIndexOf(".")).toLowerCase();
        if (!ALLOWED_EXT.contains(ext)) {
            throw new BusinessException("只支持 jpg/png/gif/webp 图片");
        }


        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
            throw new BusinessException("只支持 jpg/png/gif/webp 图片");
        }


        String newFileName = UUID.randomUUID() + ext;


        try {
            Files.createDirectories(Paths.get(uploadDir));
            file.transferTo(Paths.get(uploadDir , newFileName).toFile());
        } catch (IOException e) {
            log.error("文件保存失败", e);
            throw new BusinessException("文件保存失败，请重试");
        }
        log.info("文件上传成功 {} → {}", originalName, newFileName);
        return "/uploads/" + newFileName;
    }
}
