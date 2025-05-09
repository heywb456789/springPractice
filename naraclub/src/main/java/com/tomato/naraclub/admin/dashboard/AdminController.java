package com.tomato.naraclub.admin.dashboard;

import com.tomato.naraclub.admin.security.AdminUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @GetMapping({"", "/"})
    public String root() {
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, @AuthenticationPrincipal AdminUserDetails user) {
        // 페이지 제목 설정
        model.addAttribute("pageTitle", "대시보드 - 나라사랑 관리자");

        // 사용자 정보 설정
        model.addAttribute("userName", user.getUsername());
        model.addAttribute("userRole", user.getAuthorities());

        // 알림 정보 설정
        model.addAttribute("newMsgCount", 24);
        model.addAttribute("notificationCount", 5);

        // 활성 메뉴 설정
        model.addAttribute("activeMenu", "dashboards");
        model.addAttribute("activeSubmenu", "ecommerce");

        return "admin/dashboard";
    }
}


