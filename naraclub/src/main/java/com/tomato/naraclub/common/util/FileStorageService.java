package com.tomato.naraclub.common.util;

import com.tomato.naraclub.common.code.StorageCategory;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
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
     * FTP 서버에 파일 업로드
     *
     * @param file     업로드할 MultipartFile
     * @param category 저장 카테고리별 하위 폴더 이름
     * @param postId   게시글 ID (하위 폴더용)
     * @return 클라이언트에서 접근할 URL 경로 (예: "/uploads/images/123/abc.jpg")
     */
    public String upload(MultipartFile file, StorageCategory category, Long postId) {
        String ext = FilenameUtils.getExtension(file.getOriginalFilename());
        String filename = UUID.randomUUID() + "." + ext;
        String remoteDir = String.join("/",
            uploadRoot,
            category.getFolder(),
            postId.toString()
        );
        String remoteFile = remoteDir + "/" + filename;

        FTPClient ftp = new FTPClient();
        try (InputStream in = file.getInputStream()) {
            ftp.connect(ftpHost, ftpPort);
            if (!ftp.login(ftpUser, ftpPassword)) {
                throw new IOException("FTP 로그인 실패: " + ftpUser);
            }

            ftp.enterLocalPassiveMode();
            ftp.setFileType(FTP.BINARY_FILE_TYPE);

            // 디렉토리 계층 생성
            String[] dirs = remoteDir.split("/");
            String path = "";
            for (String dir : dirs) {
                if (dir.isEmpty()) {
                    continue;
                }
                path += "/" + dir;
                if (!ftp.changeWorkingDirectory(path)) {
                    if (!ftp.makeDirectory(path)) {
                        throw new IOException("FTP 디렉토리 생성 실패: " + path);
                    }
                }
            }

            // 파일 업로드
            if (!ftp.storeFile(remoteFile, in)) {
                throw new IOException("FTP 파일 전송 실패: " + remoteFile);
            }

            ftp.logout();

            // 반환할 URL 경로 (웹에서 /uploads/... 로 접근 가능하도록)
            return remoteFile;
        } catch (IOException e) {
            throw new UncheckedIOException("FTP 파일 저장 실패", e);
        } finally {
            if (ftp.isConnected()) {
                try {
                    ftp.disconnect();
                } catch (IOException ignored) {
                }
            }
        }
    }

    /**
     * FTP 서버에서 파일 삭제
     *
     * @param urlPath 클라이언트에 반환된 URL 경로
     * @return 삭제 성공 여부
     */
    public boolean delete(String urlPath) {

        FTPClient ftp = new FTPClient();
        try {
            ftp.connect(ftpHost, ftpPort);
            if (!ftp.login(ftpUser, ftpPassword)) {
                throw new IOException("FTP 로그인 실패: " + ftpUser);
            }
            ftp.enterLocalPassiveMode();

            // 2) 파일 삭제
            boolean deleted = ftp.deleteFile(urlPath);

            // 3) 디렉토리가 비어있다면(선택) 디렉토리도 삭제
            //    예: /uploads/images/123/ 이 비어있으면 삭제
            if (deleted) {
                String dir = urlPath.substring(0, urlPath.lastIndexOf('/'));
                // FTPClient.removeDirectory는 빈 디렉토리만 제거
                ftp.removeDirectory(dir);
            }

            ftp.logout();
            return deleted;
        } catch (IOException e) {
            throw new UncheckedIOException("FTP 파일 삭제 실패: " + urlPath, e);
        } finally {
            if (ftp.isConnected()) {
                try {
                    ftp.disconnect();
                } catch (IOException ignored) {
                }
            }
        }
    }
}
