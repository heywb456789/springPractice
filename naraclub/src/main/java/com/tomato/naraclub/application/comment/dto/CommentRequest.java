// CreateCommentRequest.java
package com.tomato.naraclub.application.comment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentRequest {

    @Schema(description = "댓글 내용", example = "좋은 글이네요!")
    @NotBlank
    private String content;
}
