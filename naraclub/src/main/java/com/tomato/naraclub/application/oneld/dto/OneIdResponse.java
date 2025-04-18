package com.tomato.naraclub.application.oneld.dto;

import lombok.Data;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.application.member.dto
 * @fileName : OneIdResponse
 * @date : 2025-04-18
 * @description :
 * @AUTHOR : MinjaeKim
 */
@Data
public class OneIdResponse {
    private boolean result;
    private int code;
    private String message;
    private OneIdValue value;
}
