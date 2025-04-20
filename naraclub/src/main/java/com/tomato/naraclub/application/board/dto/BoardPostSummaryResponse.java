package com.tomato.naraclub.application.board.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

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