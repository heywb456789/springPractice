package com.tomato.naraclub.application.original.dto;

import com.tomato.naraclub.application.original.code.OriginalCategory;
import com.tomato.naraclub.application.original.code.OriginalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VideoResponse {
    private Long videoId;
    private String title;
    private String description;
    private OriginalType type;
    private OriginalCategory category;
    private String thumbnailUrl;
    private String videoUrl;
    private Integer durationSec;
    private Long viewCount;
    private LocalDateTime publishedAt;
    private boolean isPublic;
    private boolean isHot;
    private boolean isNew;
}