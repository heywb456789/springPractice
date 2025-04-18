package com.tomato.naraclub.application.member.dto;

import com.tomato.naraclub.application.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MemberResponse {
    private Long id;
    private String userKey;
    private String phoneNumber;
    private String status;

    public static MemberResponse from(Member m) {
        return new MemberResponse(
            m.getId(),
            m.getUserKey(),
            m.getPhoneNumber(),
            m.getStatus().name()
        );
    }
}