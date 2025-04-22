package com.tomato.naraclub.application.comment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentResponse {

    @Schema(description = "댓글 ID")
    private Long commentId;

    @Schema(description = "작성자 ID")
    private Long authorId;

    @Schema(description = "작성자 이름")
    private String authorName;

    @Schema(description = "댓글 내용")
    private String content;

    @Schema(description = "isMine")
    private boolean isMine;

    @Schema(description = "생성 시간")
    private LocalDateTime createdAt;

    @Schema(description = "수정 시간")
    private LocalDateTime updatedAt;
}
