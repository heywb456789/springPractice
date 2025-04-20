package com.tomato.naraclub.application.board.dto;

import com.tomato.naraclub.application.comment.dto.CommentDto;
import lombok.Builder;
import lombok.Data;

import java.util.List;

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