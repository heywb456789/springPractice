package com.tomato.naraclub.admin.original.dto;

import com.tomato.naraclub.application.original.code.OriginalCategory;
import com.tomato.naraclub.application.original.code.OriginalType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VideoUpdateRequest {

    @NotNull(message = "videoId 는 필수입니다.")
    private Long videoId;

    @NotBlank(message = "제목은 빈 값일 수 없습니다.")
    private String title;

    @Size(max = 2000, message = "설명은 최대 2000자까지 가능합니다.")
    private String description;

    @NotNull(message = "타입을 선택해주세요.")
    private OriginalType type;

    @NotNull(message = "카테고리를 선택해주세요.")
    private OriginalCategory category;

    // 비즈니스 로직으로 URL 갱신하기 때문에, 검증은 선택적으로 처리
    private String thumbnailUrl;
    private String videoUrl;

    @NotNull(message = "영상 길이는 필수입니다.")
    private Integer durationSec;

    @NotNull(message = "공개 여부를 선택해주세요.")
    private Boolean isPublic;

    @NotNull(message = "핫 여부를 선택해주세요.")
    private Boolean isHot;

    // youtubeId 는 빈 허용
    private String youtubeUrl;
    private String youtubeId;

    @NotNull(message = "공개일시를 입력해주세요.")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime publishedAt;

    // 파일은 수정 시 선택적
    private MultipartFile videoFile;
    private MultipartFile thumbnailFile;

    private String uploadType;

    // 유효성 검사 메서드
    public boolean isYouTubeUpload() {
        return "youtube".equals(uploadType);
    }

    public boolean isFileUpload() {
        return "file".equals(uploadType);
    }

    // YouTube URL이 유효한지 확인
    public boolean hasValidYouTubeUrl() {
        return youtubeUrl != null && !youtubeUrl.trim().isEmpty();
    }

    // 파일 업로드가 유효한지 확인
    public boolean hasValidFiles() {
        return videoFile != null && !videoFile.isEmpty() &&
               thumbnailFile != null && !thumbnailFile.isEmpty();
    }

}
