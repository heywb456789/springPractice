package com.tomato.naraclub.application.auth.controller;

import com.tomato.naraclub.application.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.application.auth.controller
 * @fileName : AuthController
 * @date : 2025-05-30
 * @description :
 * @AUTHOR : MinjaeKim
 */
@Controller
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "인증 API", description = "모바일 웹 전용 로그인/회원가입/토큰 관리 API")
public class AuthController {
    private final AuthService authService;

    @Operation(
        summary = "Nice PASS 신원인증 결과 수신 CallBack",
        description = "Call Back 수신 / 웹 전용"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "토큰 유효"),
        @ApiResponse(responseCode = "401", description = "토큰 무효")
    })
    @GetMapping("/pass/callback")
    public String handleCallback(
            @RequestParam("token_version_id") String tokenVersionId,
            @RequestParam("enc_data") String encData,
            @RequestParam("integrity_value") String integrityValue)
    {
        return authService.handleCallback(tokenVersionId, encData, integrityValue);
    }
}
