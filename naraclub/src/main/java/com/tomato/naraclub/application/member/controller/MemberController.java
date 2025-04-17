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

    @PostMapping
    public ResponseEntity<MemberResponse> register(@RequestBody @Valid MemberRegisterRequest req) {
        var member = memberService.register(req.getOneId(), req.getPhoneNumber());
        return ResponseEntity.ok(new MemberResponse(
                member.getMember_no(),
                member.getOneId(),
                member.getPhoneNumber(),
                member.getStatus().name()
        ));
    }

    @PostMapping("/{id}/verify")
    public ResponseEntity<MemberResponse> verify(@PathVariable Long id) {
        var member = memberService.verify(id);
        return ResponseEntity.ok(new MemberResponse(
                member.getMember_no(),
                member.getOneId(),
                member.getPhoneNumber(),
                member.getStatus().name()
        ));
    }
}