package com.tomato.naraclub.common.util;

import com.tomato.naraclub.admin.original.dto.Base64ImageData;
import com.tomato.naraclub.common.code.StorageCategory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Base64;
import java.util.UUID;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class FileStorageService {

    @Value("${spring.app.upload.ftp.host}")
    private String ftpHost;

    @Value("${spring.app.upload.ftp.port:21}")
    private int ftpPort;

    @Value("${spring.app.upload.ftp.user}")
    private String ftpUser;

    @Value("${spring.app.upload.ftp.password}")
    private String ftpPassword;

    @Value("${spring.app.upload.root:/uploads}")
    private String uploadRoot;

    /**
     * MultipartFile 업로드 (변경 없음)
     */
    public String upload(MultipartFile file, StorageCategory category, Long postId) {
        String ext = FilenameUtils.getExtension(file.getOriginalFilename());
        String filename = UUID.randomUUID() + "." + ext;
        return storeFile(category, postId, filename, () -> file.getInputStream());
    }

    /**
     * 순수 Base64 문자열 업로드 (data URI 형태도 지원)
     */
    public String uploadBase64Image(String base64Data,
                                    StorageCategory category,
                                    Long postId) {
        String dataPart;
        String ext = "png";
        if (base64Data.startsWith("data:")) {
            String[] parts = base64Data.split(",", 2);
            String meta = parts[0];      // ex) data:image/jpeg;base64
            dataPart = parts.length > 1 ? parts[1] : "";
            String mime = meta.substring(5, meta.indexOf(';'));
            switch (mime) {
                case "image/jpeg": ext = "jpg"; break;
                case "image/png":  ext = "png"; break;
                case "image/gif":  ext = "gif"; break;
                case "image/svg+xml": ext = "svg"; break;
            }
        } else {
            dataPart = base64Data;
        }

        byte[] decoded;
        try {
            decoded = Base64.getDecoder().decode(dataPart.getBytes());
        } catch (IllegalArgumentException e) {
            throw new UncheckedIOException(new IOException("유효하지 않은 Base64 데이터입니다.", e));
        }

        String filename = UUID.randomUUID() + "." + ext;
        return storeFile(category, postId, filename, () -> new ByteArrayInputStream(decoded));
    }

    /**
     * DTO 그대로 받아서 업로드 + URL 세팅
     */
    public String uploadBase64Image(Base64ImageData imageData,
                                    StorageCategory category,
                                    Long postId) {
        // 1) 확장자 지정 (DTO.imageType 예: "png", "jpeg")
        String ext = imageData.getImageType();
        if (ext == null || ext.isBlank()) {
            // fallback: mime 추출 로직 재사용
            ext = FilenameUtils.getExtension(imageData.getOriginalTag());
            if (ext == null || ext.isBlank()) {
                ext = "png";
            }
        }
        // jpeg → jpg
        if ("jpeg".equalsIgnoreCase(ext)) {
            ext = "jpg";
        }

        // 2) 파일명
        String filename = UUID.randomUUID() + "." + ext;

        // 3) FTP 저장
        String urlPath = storeFile(category, postId, filename,
            () -> new ByteArrayInputStream(imageData.decodeBase64()));

        // 4) DTO에 새 URL 세팅
        imageData.setNewImageUrl(urlPath);
        return urlPath;
    }

    /**
     * FTP 삭제 (변경 없음)
     */
    public boolean delete(String urlPath) {
        FTPClient ftp = new FTPClient();
        try {
            ftp.connect(ftpHost, ftpPort);
            if (!ftp.login(ftpUser, ftpPassword)) {
                throw new IOException("FTP 로그인 실패: " + ftpUser);
            }
            ftp.enterLocalPassiveMode();
            boolean deleted = ftp.deleteFile(urlPath);
            if (deleted) {
                String dir = urlPath.substring(0, urlPath.lastIndexOf('/'));
                ftp.removeDirectory(dir);
            }
            ftp.logout();
            return deleted;
        } catch (IOException e) {
            throw new UncheckedIOException("FTP 파일 삭제 실패: " + urlPath, e);
        } finally {
            if (ftp.isConnected()) {
                try { ftp.disconnect(); } catch (IOException ignored) {}
            }
        }
    }

    // ─────────────────────────────────────────────────────────────────────
    // 내부 헬퍼
    // ─────────────────────────────────────────────────────────────────────

    private String storeFile(StorageCategory category,
                             Long postId,
                             String filename,
                             StreamSupplier supplier) {
        String remoteDir  = String.join("/", uploadRoot, category.getFolder(), postId.toString());
        String remoteFile = remoteDir + "/" + filename;
        FTPClient ftp = new FTPClient();
        try (InputStream in = supplier.get()) {
            ftp.connect(ftpHost, ftpPort);
            if (!ftp.login(ftpUser, ftpPassword)) {
                throw new IOException("FTP 로그인 실패: " + ftpUser);
            }
            ftp.enterLocalPassiveMode();
            ftp.setFileType(FTP.BINARY_FILE_TYPE);

            // 디렉토리 생성
            String path = "";
            for (String dir : remoteDir.split("/")) {
                if (dir.isEmpty()) continue;
                path += "/" + dir;
                if (!ftp.changeWorkingDirectory(path) && !ftp.makeDirectory(path)) {
                    throw new IOException("디렉토리 생성 실패: " + path);
                }
            }
            // 파일 업로드
            if (!ftp.storeFile(remoteFile, in)) {
                throw new IOException("파일 전송 실패: " + remoteFile);
            }
            ftp.logout();
            return remoteFile;
        } catch (IOException e) {
            throw new UncheckedIOException("FTP 파일 저장 실패", e);
        } finally {
            if (ftp.isConnected()) {
                try { ftp.disconnect(); } catch (IOException ignored) {}
            }
        }
    }

    @FunctionalInterface
    private interface StreamSupplier {
        InputStream get() throws IOException;
    }
}
