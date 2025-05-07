package com.tomato.naraclub.admin.original.dto;

import com.tomato.naraclub.application.comment.dto.CommentResponse;
import com.tomato.naraclub.application.original.code.OriginalCategory;
import com.tomato.naraclub.application.original.code.OriginalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewsArticleResponse {
    private Long articleId;
    private String title;
    private String subTitle;
    private String content;
    private OriginalType type;
    private OriginalCategory category;
    private String thumbnailUrl;
    private Long viewCount;
    private Long commentCount;
    private LocalDateTime publishedAt;
    private boolean isPublic;
    private boolean isHot;
    private boolean isNew;
    private String authorName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<String> imageUrls;
    private List<CommentResponse> comments;
    private boolean isDeleted;
}
