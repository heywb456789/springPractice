package com.tomato.naraclub.application.member.controller;

import com.tomato.naraclub.application.member.dto.AuthRequest;
import com.tomato.naraclub.application.member.dto.AuthResponse;
import com.tomato.naraclub.application.member.service.AuthService;
import com.tomato.naraclub.application.oneld.TomatoAuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final TomatoAuthService tomatoService;

    @PostMapping("/login")
    public Mono<ResponseEntity<AuthResponse>> login(@RequestBody AuthRequest req) {
        return tomatoService.authenticate(req.getPhoneNumber(), req.getPassword())
            .flatMap(resp -> {
                if (!resp.isResult()) {
                    return Mono.just(ResponseEntity
                        .status(401)
                        .body(new AuthResponse("External authentication failed")));
                }
                return Mono.just(ResponseEntity.ok(authService.createToken(resp)));
            });
    }
}