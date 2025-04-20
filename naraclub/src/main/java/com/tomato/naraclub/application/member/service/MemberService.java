package com.tomato.naraclub.application.member.service;

import com.tomato.naraclub.application.member.dto.MemberDTO;

public interface MemberService {

    MemberDTO enrollInviteCode(String inviteCode);
}
