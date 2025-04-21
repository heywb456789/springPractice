package com.tomato.naraclub.application.video.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoDto {
    private Long id;
    private String youtubeId;
    private String title;
    private String description;
    private String url;
    private String thumbnailUrl;
    private Long viewCount;
    private Integer duration;
    private String category;
    private LocalDateTime createdAt;

    // 클라이언트에서 사용하기 쉽도록 추가 메서드
    public String getFormattedDuration() {
        if (duration == null) return "";
        int minutes = duration / 60;
        int seconds = duration % 60;
        return String.format("%d:%02d", minutes, seconds);
    }

    public String getYoutubeEmbedUrl() {
        return "https://www.youtube.com/embed/" + youtubeId;
    }
}