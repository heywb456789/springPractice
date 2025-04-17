package com.tomato.naraclub.application.member.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MemberResponse {
    private Long id;
    private String oneId;
    private String phoneNumber;
    private String status;
}