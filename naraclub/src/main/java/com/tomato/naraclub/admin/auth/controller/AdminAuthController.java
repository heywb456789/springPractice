package com.tomato.naraclub.admin.auth.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/admin/auth")
public class AdminAuthController {

    @GetMapping("/login")
    public String loginPage(){
        return "admin/login/login";
    }

    @GetMapping("/register")
    public String registerPage(){
        return "admin/login/register";
    }

    @GetMapping("/logout")
    public void logout(HttpServletRequest req, HttpServletResponse response) throws IOException {

        boolean isHttps = req.isSecure();
        // ACCESS_TOKEN 완전 삭제
        ResponseCookie accessCookie = ResponseCookie.from("ACCESS_TOKEN", "")
            .path("/")
            .httpOnly(true)
            .secure(isHttps)
            .sameSite("Strict")
            .maxAge(0)
            .build();
        // REFRESH_TOKEN 완전 삭제
        ResponseCookie refreshCookie = ResponseCookie.from("REFRESH_TOKEN", "")
            .path("/")
            .httpOnly(true)
            .secure(isHttps)
            .sameSite("Strict")
            .maxAge(0)
            .build();

        response.setHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        // 로그인 페이지로 리디렉션 (302)
        response.sendRedirect("/admin/auth/login");
    }
}
