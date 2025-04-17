package com.tomato.naraclub.application.member.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class MemberRegisterRequest {
    @NotBlank
    private String oneId;

    @NotBlank
    private String phoneNumber;
}