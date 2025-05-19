package com.tomato.naraclub.admin.point.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.admin.point.dto
 * @fileName : MemberRevokeParam
 * @date : 2025-05-19
 * @description :
 * @AUTHOR : MinjaeKim
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberRevokeParam {

    private Long memberId;
    private double amount;
    private String reason;
}
