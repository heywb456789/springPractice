package com.tomato.naraclub.application.member.service;

import com.tomato.naraclub.application.board.dto.ShareResponse;
import com.tomato.naraclub.application.member.dto.MemberDTO;
import com.tomato.naraclub.application.member.dto.MemberUpdateRequest;
import com.tomato.naraclub.application.security.MemberUserDetails;

public interface MemberService {


    MemberDTO enrollInviteCode(String inviteCode, MemberUserDetails userDetails);

    ShareResponse getShareInfo(String code);

    MemberDTO updateName(MemberUpdateRequest request, MemberUserDetails userDetails);
}
