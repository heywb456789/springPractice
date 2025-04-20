package com.tomato.naraclub.application.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CommentDto {
    private String authorName;
    private String content;
    private LocalDateTime createdAt;
}
