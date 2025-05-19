package com.tomato.naraclub.admin.auth.controller;

import com.tomato.naraclub.admin.auth.dto.AdminAuthRequest;
import com.tomato.naraclub.admin.auth.dto.AdminAuthResponseDTO;
import com.tomato.naraclub.admin.auth.service.AdminAuthService;
import com.tomato.naraclub.admin.security.AdminUserDetails;
import com.tomato.naraclub.application.auth.dto.AuthResponseDTO;
import com.tomato.naraclub.common.dto.ResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// com.tomato.naraclub.application.admin.controller.AdminAuthController.java
@RestController
@RequestMapping("/admin/auth")
@RequiredArgsConstructor
public class AdminAuthRestController {
    private final AdminAuthService authService;

    @PostMapping("/login")
    public ResponseDTO<AdminAuthResponseDTO> login(
        @RequestBody AdminAuthRequest req,
        HttpServletRequest request) {

        return ResponseDTO.ok(authService.createToken(req, request));
    }

    @PostMapping("/register")
    public ResponseDTO<AdminAuthResponseDTO> register(
        @RequestBody AdminAuthRequest req,
        HttpServletRequest request){
        return ResponseDTO.ok(authService.createUserAndToken(req, request));
    }

    @GetMapping("/check/username")
    public ResponseDTO<Boolean> checkUsername(@RequestParam String username) {
        return ResponseDTO.ok(authService.checkUserName(username));
    }

    @PostMapping("/refresh")
    public ResponseDTO<AdminAuthResponseDTO> refreshToken(@RequestBody Map<String, String> request) {
        return ResponseDTO.ok(authService.refreshToken(request.get("refreshToken")));
    }

    @GetMapping("/debug")
    public ResponseEntity<?> debug(@AuthenticationPrincipal AdminUserDetails admin) {
    return ResponseEntity.ok(Map.of(
        "username", admin.getUsername(),
        "role", admin.getAdmin().getRole(),
        "authorities", admin.getAuthorities()
    ));
}
}
