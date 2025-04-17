package com.tomato.naraclub.application.member.service;

import com.tomato.naraclub.application.member.repository.MemberRepository;
import com.tomato.naraclub.application.member.entity.Member;
import com.tomato.naraclub.common.code.MemberStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;

    @Override
    public Member register(String oneId, String phoneNumber) {
        Member member = Member.builder()
                .oneId(oneId)
                .phoneNumber(phoneNumber)
                .status(MemberStatus.TEMPORARY)
                .build();
        return memberRepository.save(member);
    }

    @Override
    public Member verify(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));
        member.setStatus(MemberStatus.ACTIVE);
        return member;
    }
}