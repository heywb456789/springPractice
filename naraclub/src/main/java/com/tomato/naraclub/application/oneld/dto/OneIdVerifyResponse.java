package com.tomato.naraclub.application.oneld.dto;

import lombok.Data;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.application.oneld.dto
 * @fileName : OneIdVerifyResponse
 * @date : 2025-04-21
 * @description :
 * @AUTHOR : MinjaeKim
 */
@Data
public class OneIdVerifyResponse {
    private boolean result;
    private int code;
    private String message;
    private boolean value;
}
