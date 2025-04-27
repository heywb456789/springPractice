package com.tomato.naraclub.application.vote.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tomato.naraclub.application.vote.entity.VoteRecord;
import java.time.LocalDateTime;
import java.util.List;

import lombok.*;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.application.vote.dto
 * @fileName : VotePostResponse
 * @date : 2025-04-23
 * @description :
 * @AUTHOR : MinjaeKim
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VotePostResponse {
    private Long votePostId;
    private Long authorId;
    private String question;
    private List<VoteOptionDTO> voteOptions;
    private List<VoteCommentsDTO> voteComments;
    private Long votedId;
    private Long commentCount;
    private Long viewCount;
    private Long voteCount;
    private boolean isNew;
    private boolean isVoted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
