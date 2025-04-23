package com.tomato.naraclub.application.original.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class VideoDetailResponse {
    private Long id;
    private String title;
    private String description;
    private String videoUrl;
    private Integer durationSec;
    private Long viewCount;
    private LocalDateTime publishedAt;
    private boolean isHot;
}