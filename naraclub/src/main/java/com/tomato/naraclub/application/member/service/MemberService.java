package com.tomato.naraclub.application.member.service;

import com.tomato.naraclub.application.member.entity.Member;

public interface MemberService {
    Member register(String oneId, String phoneNumber);
    Member verify(Long memberId);
}
