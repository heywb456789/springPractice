package com.tomato.naraclub.application.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthRequestDTO {
    @NotBlank
    private String phoneNumber;

    @NotBlank
    private String password;

    private boolean autoLogin;

    private String verificationCode;

    @NotBlank
    private String userKey;

    @NotBlank
    private String name;

    private Boolean marketingAgree;
}