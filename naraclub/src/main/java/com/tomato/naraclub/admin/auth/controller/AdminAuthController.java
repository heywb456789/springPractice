package com.tomato.naraclub.admin.auth.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.admin.auth.controller
 * @fileName : AdminAuthController
 * @date : 2025-04-28
 * @description :
 * @AUTHOR : MinjaeKim
 */
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
    public String logout(HttpServletResponse response) {
        // ACCESS_TOKEN 쿠키 제거
        Cookie accessCookie = new Cookie("ACCESS_TOKEN", "");
        accessCookie.setPath("/");
        accessCookie.setHttpOnly(true);
        accessCookie.setMaxAge(0);
        response.addCookie(accessCookie);

        // REFRESH_TOKEN 쿠키 제거
        Cookie refreshCookie = new Cookie("REFRESH_TOKEN", "");
        refreshCookie.setPath("/");
        refreshCookie.setHttpOnly(true);
        refreshCookie.setMaxAge(0);
        response.addCookie(refreshCookie);

        // 로그인 페이지로 리다이렉트
        return "redirect:/admin/auth/login";
    }
}
