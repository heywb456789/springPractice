package com.tomato.naraclub.admin.board.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.admin.board.dto
 * @fileName : AdminBoardDto
 * @date : 2025-04-28
 * @description :
 * @AUTHOR : MinjaeKim
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminBoardDto {
    private Long boardId;
    private Long authorId;
    private String authorName;
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
