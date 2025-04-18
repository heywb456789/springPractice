package com.tomato.naraclub.application.member.service;

import com.tomato.naraclub.application.member.dto.MemberResponse;
import com.tomato.naraclub.application.member.entity.Member;

public interface MemberService {

    MemberResponse enrollInviteCode(String inviteCode);
}
