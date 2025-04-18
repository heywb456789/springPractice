package com.tomato.naraclub.application.member.service;

import com.tomato.naraclub.application.member.dto.AuthResponse;
import com.tomato.naraclub.application.oneld.dto.OneIdResponse;
import com.tomato.naraclub.application.member.entity.Member;
import com.tomato.naraclub.application.member.repository.MemberRepository;
import com.tomato.naraclub.application.security.JwtTokenProvider;
import com.tomato.naraclub.common.code.MemberRole;
import com.tomato.naraclub.common.code.MemberStatus;
import com.tomato.naraclub.common.util.InviteCodeGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.application.member.service
 * @fileName : AuthServiceImpl
 * @date : 2025-04-18
 * @description :
 * @AUTHOR : MinjaeKim
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final JwtTokenProvider tokenProvider;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public AuthResponse createToken(OneIdResponse resp) {
        String userKey = resp.getValue().getUserKey();
        //userKey 있으면 일단은 생성 + 초대코드 + 신원 인증 관련 처리 미상태로 저장
        Member member = memberRepository.findByUserKey(userKey)
            .orElseGet(() -> {
                Member m = Member.builder()
                    .userKey(userKey)
                    .password(passwordEncoder.encode(resp.getValue().getPasswd()))
                    .phoneNumber(resp.getValue().getDecPhoneNum())
                    .inviteCode(InviteCodeGenerator.generateUnique(memberRepository))
                    .status(MemberStatus.TEMPORARY_INVITE)
                    .role(MemberRole.USER_INACTIVE)
                    .email(resp.getValue().getEmail())
                    .name(resp.getValue().getName())
                    .verified(false)
                    .profileImg(resp.getValue().getProfileImg())
                    .build();
                return memberRepository.save(m);
            });
        // JWT 생성
        return AuthResponse.builder()
            .token(tokenProvider.createToken(userKey))
            .build();
    }
}
