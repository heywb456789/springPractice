package com.tomato.naraclub.common.util;

import com.tomato.naraclub.common.code.StorageCategory;
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
public class FileStorageService {

    // 문자로 읽어오는 루트 경로
    @Value("${spring.app.upload.root}")
    private String uploadRoot;

    // 파일 저장할 실제 Path
    private Path rootLocation;

    @PostConstruct
    public void init() {
        this.rootLocation = Paths.get(uploadRoot);
    }

    public String upload(MultipartFile file, StorageCategory category, Long postId) {

        String ext = FilenameUtils.getExtension(file.getOriginalFilename());
        String filename = UUID.randomUUID() + "." + ext;

        try {
            Path targetDir = rootLocation
                    .resolve(category.getFolder())
                    .resolve(postId.toString());

            Files.createDirectories(targetDir);

            Path targetFile = targetDir.resolve(filename);
            file.transferTo(targetFile);

            String urlPath = String.join("/",
                    "uploads",                        // 앞에 / 빼고
                    category.getFolder(),
                    postId.toString(),
                    filename
            );

            return "/" +urlPath;
        } catch (IOException e) {
            throw new UncheckedIOException("파일 저장 실패", e);
        }
    }
}
