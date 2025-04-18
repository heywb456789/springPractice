package com.tomato.naraclub.application.board;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class BoardPostRequest {
    private String title;
    private String content;
    private List<String> imageUrls;
}

@Data
@Builder
public class BoardPostSummaryResponse {
    private Long id;
    private String title;
    private String authorName;
    private int views;
    private int likes;
    private int commentCount;
    private LocalDateTime createdAt;
}

@Data
@Builder
public class BoardPostDetailResponse {
    private Long id;
    private String title;
    private String content;
    private List<String> imageUrls;
    private String authorName;
    private int views;
    private int likes;
    private List<CommentDto> comments;
}
