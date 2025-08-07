package com.tomato.naraclub.application.original.dto;

import com.tomato.naraclub.application.original.code.OriginalCategory;
import com.tomato.naraclub.application.original.code.OriginalType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VideoUploadRequest {
    @NotBlank(message = "제목은 필수입니다")
    @Size(max = 200, message = "제목은 200자 이하여야 합니다")
    private String title;

    @Size(max = 2000, message = "설명은 2000자 이하여야 합니다")
    private String description;

    @NotNull(message = "타입은 필수입니다")
    private OriginalType type;

    @NotNull(message = "카테고리는 필수입니다")
    private OriginalCategory category;

    private String thumbnailUrl;

    private String videoUrl;

    @NotNull(message = "영상 길이는 필수입니다")
    @Positive(message = "영상 길이는 양수여야 합니다")
    private Integer durationSec;

    @NotNull
    private Boolean isPublic;

    private Boolean isHot;

    private String youtubeUrl;
    private String youtubeId;

    @NotBlank(message = "업로드 타입은 필수입니다")
    private String uploadType; // "file" 또는 "youtube"

    @NotNull(message = "공개 시간은 필수입니다")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime publishedAt;

    @NotNull
    private MultipartFile videoFile;

    @NotNull
    private MultipartFile thumbnailFile;

    // === 유효성 검증 메서드 ===
    @AssertTrue(message = "파일 업로드 시 비디오 파일과 썸네일 파일이 필요합니다")
    private boolean isValidFileUpload() {
        if ("file".equals(uploadType)) {
            return videoFile != null && thumbnailFile != null;
        }
        return true;
    }

    @AssertTrue(message = "유튜브 업로드 시 유튜브 URL 또는 ID가 필요합니다")
    private boolean isValidYoutubeUpload() {
        if ("youtube".equals(uploadType)) {
            return youtubeUrl != null && !youtubeUrl.trim().isEmpty();
        }
        return true;
    }
}