package com.tomato.naraclub.application.member.controller;

import com.tomato.naraclub.application.member.dto.MemberInviteRequest;
import com.tomato.naraclub.application.member.dto.MemberDTO;
import com.tomato.naraclub.application.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/invite")
    public ResponseEntity<MemberDTO> enrollInviteCode(@RequestBody MemberInviteRequest request) {
        return ResponseEntity.ok(memberService.enrollInviteCode(request.getInviteCode()));
    }

}