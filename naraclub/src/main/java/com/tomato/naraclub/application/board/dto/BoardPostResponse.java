package com.tomato.naraclub.application.board.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardPostResponse {
    private Long id;
    private Long authorId;
    private String title;
    private String content;
    private List<String> imageUrls;
    private long commentCount;
    private int views;
    private int likes;
    private int shareCount;
    private boolean isNew;
    private boolean isHot;
    private LocalDateTime createdAt;
}