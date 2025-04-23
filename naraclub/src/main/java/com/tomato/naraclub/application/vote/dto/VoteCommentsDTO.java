package com.tomato.naraclub.application.vote.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.application.vote.dto
 * @fileName : VoteCommentsDTO
 * @date : 2025-04-23
 * @description :
 * @AUTHOR : MinjaeKim
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoteCommentsDTO {
    private Long votePostId;
    private Long commentId;
    private Long authorId;
    private String authorName;
    private String content;
    private String createdAt;
}
