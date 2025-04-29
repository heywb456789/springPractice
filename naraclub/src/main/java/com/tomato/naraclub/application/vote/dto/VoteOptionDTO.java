package com.tomato.naraclub.application.vote.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.application.vote.dto
 * @fileName : VoteOptionDTO
 * @date : 2025-04-23
 * @description :
 * @AUTHOR : MinjaeKim
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoteOptionDTO {
    private Long votePostId;
    private Long optionId;
    private String optionName;
    private Long voteCount;
    private double percentage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
