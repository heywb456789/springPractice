package com.tomato.naraclub.application.member.controller;

import com.tomato.naraclub.application.member.dto.AuthRequest;
import com.tomato.naraclub.application.member.dto.AuthResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import com.tomato.naraclub.application.security.JwtTokenProvider;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authManager;
    private final JwtTokenProvider tokenProvider;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid AuthRequest req) {
        authManager.authenticate(new UsernamePasswordAuthenticationToken(req.getOneId(), ""));
        String token = tokenProvider.createToken(req.getOneId());
        return ResponseEntity.ok(new AuthResponse(token));
    }
}