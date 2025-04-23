package com.tomato.naraclub.application.original.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class VideoResponse {
    private Long id;
    private String title;
    private String thumbnailUrl;
    private Long viewCount;
    private LocalDateTime publishedAt;
    private boolean isHot;
}