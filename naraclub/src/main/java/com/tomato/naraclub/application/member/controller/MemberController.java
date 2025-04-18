package com.tomato.naraclub.application.member.controller;

import com.tomato.naraclub.application.member.dto.MemberRegisterRequest;
import com.tomato.naraclub.application.member.dto.MemberResponse;
import com.tomato.naraclub.application.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/invite")
    public ResponseEntity<MemberResponse> enrollInviteCode(@RequestBody MemberRegisterRequest request) {
        return ResponseEntity.ok(memberService.enrollInviteCode(request.getInviteCode()));
    }

}