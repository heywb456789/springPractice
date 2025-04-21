package com.tomato.naraclub.common.util;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;

@Component
public class ImageStorageService {
    private final Path root = Paths.get("uploads");

    public String upload(MultipartFile file) {
        try {
            if (!Files.exists(root)) Files.createDirectories(root);
            Path dest = root.resolve(file.getOriginalFilename()).normalize().toAbsolutePath();
            file.transferTo(dest);
            return "/uploads/" + dest.getFileName();
        } catch (IOException e) {
            throw new RuntimeException("이미지 업로드 실패", e);
        }
    }
}