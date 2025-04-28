package com.tomato.naraclub.admin.auth.dto;

import lombok.Data;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.admin.auth.dto
 * @fileName : AdminAuthRequest
 * @date : 2025-04-28
 * @description :
 * @AUTHOR : MinjaeKim
 */
@Data
public class AdminAuthRequest {

    private String username;

    private String password;

    private String email;

    private String phoneNumber;

    private String name;

    private Boolean autoLogin;
}
