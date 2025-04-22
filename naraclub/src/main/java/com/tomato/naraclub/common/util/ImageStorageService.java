package com.tomato.naraclub.common.util;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class ImageStorageService {

    // 문자로 읽어오는 루트 경로
    @Value("${spring.app.upload.root}")
    private String uploadRoot;

    // 파일 저장할 실제 Path
    private Path rootLocation;

    @PostConstruct
    public void init() {
        this.rootLocation = Paths.get(uploadRoot);
    }

    public String upload(MultipartFile file, Long postId) {
        String ext = FilenameUtils.getExtension(file.getOriginalFilename());
        String filename = UUID.randomUUID() + "." + ext;
        try {
            Path postDir = rootLocation.resolve(postId.toString());
            Files.createDirectories(postDir);

            Path target = postDir.resolve(filename);
            file.transferTo(target);

            return "/uploads/" + postId + "/" + filename;
        } catch (IOException e) {
            throw new UncheckedIOException("파일 저장 실패", e);
        }
    }
}
