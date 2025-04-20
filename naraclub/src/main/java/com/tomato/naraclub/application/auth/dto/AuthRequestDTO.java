package com.tomato.naraclub.application.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthRequestDTO {
    @NotBlank
    private String phoneNumber;

    @NotBlank
    private String password;

    private boolean autoLogin;
}