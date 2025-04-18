package com.tomato.naraclub.application.member.service;

import com.tomato.naraclub.application.member.dto.MemberResponse;
import com.tomato.naraclub.application.member.repository.MemberRepository;
import com.tomato.naraclub.application.member.entity.Member;
import com.tomato.naraclub.common.code.MemberStatus;
import com.tomato.naraclub.common.exception.BadRequestException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;


    @Override
    @Transactional
    public MemberResponse enrollInviteCode(String inviteCode) {
        // 1) 초대 코드로 추천인(Member) 조회
        Member inviter = memberRepository.findByInviteCode(inviteCode)
            .orElseThrow(() -> new BadRequestException("존재하지 않는 초대 코드입니다. 다시 시도해주세요."));

        // 2) 현재 로그인된 회원 조회
        String currentUserKey = SecurityContextHolder.getContext()
            .getAuthentication().getName();
        Member current = memberRepository.findByUserKey(currentUserKey)
            .orElseThrow(() -> new BadRequestException("유저 정보를 찾을 수 없습니다."));

        // 3) 이미 초대 코드가 등록된 경우 예외
        if (current.getStatus() == MemberStatus.ACTIVE) {
            throw new BadRequestException("이미 초대 코드가 등록되었습니다.");
        }

        // 4) 추천인과 연결 및 상태 변경
        current.setStatus(MemberStatus.ACTIVE);
        current.setInviter(inviter);

        // 5) 응답 DTO 반환
        return MemberResponse.from(current);
    }
}