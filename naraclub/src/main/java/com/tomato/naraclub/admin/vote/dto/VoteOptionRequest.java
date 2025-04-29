package com.tomato.naraclub.admin.vote.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.admin.vote.dto
 * @fileName : VoteOptionRequest
 * @date : 2025-04-29
 * @description :
 * @AUTHOR : MinjaeKim
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoteOptionRequest {

    private Long optionId;
    private String optionName;
}
