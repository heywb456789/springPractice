package com.tomato.naraclub.application.original.dto;

import com.tomato.naraclub.application.original.code.OriginalCategory;
import com.tomato.naraclub.application.original.code.OriginalType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VideoUploadRequest {
    @NotBlank
    private String title;

    @Size(max = 2000)
    private String description;

    @NotNull
    private OriginalType type;

    @NotNull
    private OriginalCategory category;

    @NotBlank @Size(max = 500)
    private String thumbnailUrl;

    @NotBlank
    private String videoUrl;

    @NotNull
    private Integer durationSec;

    @NotNull
    private Boolean isPublic;

    @NotBlank
    private String youtubeId;

    @NotNull
    private LocalDateTime publishedAt;

    @NotNull
    private MultipartFile videoFile;

    @NotNull
    private MultipartFile thumbnailFile;
}