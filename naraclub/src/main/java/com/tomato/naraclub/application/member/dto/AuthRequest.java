package com.tomato.naraclub.application.member.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthRequest {
    @NotBlank
    private String oneId;
}