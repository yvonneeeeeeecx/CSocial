package com.social.csocial.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping({"/api/upload", "/upload"})
@CrossOrigin(origins = "*")
public class UploadController {


    @Value("${csocial.upload-dir:uploads}")
    private String uploadDir;

    @Operation(summary = "上传图片", description = "上传图片并返回可访问链接")
    @PostMapping("/image")
    public ResponseEntity<Map<String, Object>> uploadImage(@RequestParam("file") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();

        if (file == null || file.isEmpty()) {
            response.put("success", false);
            response.put("message", "文件不能为空");
            return ResponseEntity.badRequest().body(response);
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.toLowerCase().startsWith("image/")) {
            response.put("success", false);
            response.put("message", "仅支持图片上传");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            Path dirPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(dirPath);

            String originalName = StringUtils.cleanPath(file.getOriginalFilename());
            String ext = "";
            int dotIndex = originalName.lastIndexOf('.');
            if (dotIndex >= 0) {
                ext = originalName.substring(dotIndex);
            }
            String filename = UUID.randomUUID() + ext;

            Path targetPath = dirPath.resolve(filename);
            file.transferTo(targetPath.toFile());

            Map<String, Object> data = new HashMap<>();
            data.put("url", "/api/uploads/" + filename);

            data.put("filename", filename);

            response.put("success", true);
            response.put("message", "上传成功");
            response.put("data", data);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "上传失败");
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
