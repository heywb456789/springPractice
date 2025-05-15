package com.tomato.naraclub.application.auth.controller;

import com.tomato.naraclub.application.auth.dto.AuthRequestDTO;
import com.tomato.naraclub.application.auth.dto.AuthResponseDTO;
import com.tomato.naraclub.application.auth.service.AuthService;
import com.tomato.naraclub.application.member.dto.MemberDTO;
import com.tomato.naraclub.application.member.entity.Member;
import com.tomato.naraclub.application.member.repository.MemberRepository;
import com.tomato.naraclub.application.oneld.dto.OneIdResponse;
import com.tomato.naraclub.application.oneld.dto.OneIdVerifyResponse;
import com.tomato.naraclub.application.oneld.service.TomatoAuthService;
import com.tomato.naraclub.application.security.MemberUserDetails;
import com.tomato.naraclub.common.code.MemberStatus;
import com.tomato.naraclub.common.code.ResponseStatus;
import com.tomato.naraclub.common.dto.ResponseDTO;
import com.tomato.naraclub.common.exception.BadRequestException;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "사용자 정보 API", description = "사용자 정보 CRUD API")
public class AuthController {

    private final AuthService authService;
    private final TomatoAuthService tomatoService;

    private final MemberRepository memberRepository;

    // Controller
    @GetMapping("/validate")
    public ResponseEntity<Void> validate(Authentication auth) {
        // 유효한 요청이면 204, 아니면 401을 Filter 처리
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseDTO<MemberDTO> me(@AuthenticationPrincipal MemberUserDetails user) {
        return ResponseDTO.ok(authService.me(user));
    }


    @PostMapping("/login")
    public Mono<ResponseDTO<?>> login(@RequestBody AuthRequestDTO req,
        HttpServletRequest servletRequest) {
        return tomatoService.authenticate(req.getPhoneNumber(), req.getPassword())
            .flatMap(resp -> {
                if (!resp.isResult()) {
                    return Mono.just(ResponseDTO.error(ResponseStatus.UNAUTHORIZED));
                }

                AuthResponseDTO responseDTO = authService.createToken(resp, req, servletRequest);
                return Mono.just(ResponseDTO.ok(responseDTO));
            })
            .onErrorResume(ResponseStatusException.class, ex -> {
                HttpStatusCode status = ex.getStatusCode();
                if (status == HttpStatus.UNAUTHORIZED) {
                    // 401 전용 처리
                    return Mono.just(ResponseDTO.error(ResponseStatus.UNAUTHORIZED_ONE_ID));
                } else if (status == HttpStatus.INTERNAL_SERVER_ERROR) {
                    // 403 전용 처리
                    return Mono.just(ResponseDTO.error(ResponseStatus.INTERNAL_SERVER_ERROR));
                }
                // 그 외는 그대로 다시 던지기
                return Mono.error(ex);
            });
    }

    @PostMapping("/smsCert/send")
    public Mono<ResponseDTO<OneIdResponse>> sendSmsCert(@RequestBody AuthRequestDTO req) {
        return tomatoService.sendSmsCert(req)
            .map(ResponseDTO::ok);
    }

    @PostMapping("/smsCert/verify")
    public Mono<ResponseDTO<OneIdVerifyResponse>> verifySmsCert(@RequestBody AuthRequestDTO req) {
        return tomatoService.verifySmsCert(req)
            .map(ResponseDTO::ok);
    }

    @PostMapping("/register")
    public ResponseDTO<AuthResponseDTO> register(@RequestBody @Valid AuthRequestDTO req,
        HttpServletRequest servletRequest) {

        OneIdResponse resp = tomatoService.createOneId(req);

        if (resp == null || !resp.isResult()) {
            throw new BadRequestException("외부 회원 인증 실패");
        }

        return ResponseDTO.ok(authService.createToken(resp, req, servletRequest));
    }

    @DeleteMapping("/logout")
    public ResponseDTO<?> logout(@AuthenticationPrincipal MemberUserDetails userDetails,
        HttpServletRequest request) {
        authService.logout(userDetails, request);

        return ResponseDTO.ok();
    }


    @PostMapping("/refresh")
    public ResponseDTO<AuthResponseDTO> refreshToken(@RequestBody Map<String, String> request, HttpServletRequest servletRequest) {
        return ResponseDTO.ok(authService.refreshToken(request.get("refreshToken"), servletRequest));
    }
}