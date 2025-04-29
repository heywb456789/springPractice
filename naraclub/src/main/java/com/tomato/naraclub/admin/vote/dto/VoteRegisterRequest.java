package com.tomato.naraclub.admin.vote.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.admin.vote.dto
 * @fileName : VoteRegisterReqeust
 * @date : 2025-04-29
 * @description :
 * @AUTHOR : MinjaeKim
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoteRegisterRequest {

    private Long voteId;
    private String question;
    private List<VoteOptionRequest> voteOptions;
    private LocalDate startDate;
    private LocalDate endDate;

}
