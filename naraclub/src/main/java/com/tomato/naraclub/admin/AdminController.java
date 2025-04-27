package com.tomato.naraclub.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @GetMapping({"", "/", "/dashboard"})
    public String dashboard(Model model) {
        // 페이지 제목 설정
        model.addAttribute("pageTitle", "대시보드 - Spark Admin");

        // 사용자 정보 설정
        model.addAttribute("userName", "Linda Miller");
        model.addAttribute("userRole", "Front-end Developer");
        model.addAttribute("userAvatar", "/assets/admin/images/default-avatar.png");

        // 알림 정보 설정
        model.addAttribute("newMsgCount", 24);
        model.addAttribute("notificationCount", 5);

        // 활성 메뉴 설정
        model.addAttribute("activeMenu", "dashboards");
        model.addAttribute("activeSubmenu", "ecommerce");

        return "admin/dashboard/ecommerce";
    }

    @GetMapping("/board")
    public String board(Model model) {
        // 페이지 제목 설정
        model.addAttribute("pageTitle", "게시판 관리 - Spark Admin");

        // 사용자 정보 설정 (공통)
        model.addAttribute("userName", "Linda Miller");
        model.addAttribute("userRole", "Front-end Developer");
        model.addAttribute("userAvatar", "/assets/admin/images/default-avatar.png");

        // 활성 메뉴 설정
        model.addAttribute("activeMenu", "board");

        return "admin/board/index";
    }

    @GetMapping("/news")
    public String news(Model model) {
        // 페이지 제목 설정
        model.addAttribute("pageTitle", "뉴스 관리 - Spark Admin");

        // 사용자 정보 설정 (공통)
        model.addAttribute("userName", "Linda Miller");
        model.addAttribute("userRole", "Front-end Developer");
        model.addAttribute("userAvatar", "/assets/admin/images/default-avatar.png");

        // 활성 메뉴 설정
        model.addAttribute("activeMenu", "news");

        return "admin/news/index";
    }

    @GetMapping("/video")
    public String video(Model model) {
        // 페이지 제목 설정
        model.addAttribute("pageTitle", "비디오 관리 - Spark Admin");

        // 사용자 정보 설정 (공통)
        model.addAttribute("userName", "Linda Miller");
        model.addAttribute("userRole", "Front-end Developer");
        model.addAttribute("userAvatar", "/assets/admin/images/default-avatar.png");

        // 활성 메뉴 설정
        model.addAttribute("activeMenu", "video");

        return "admin/video/index";
    }

    @GetMapping("/vote")
    public String vote(Model model) {
        // 페이지 제목 설정
        model.addAttribute("pageTitle", "투표 관리 - Spark Admin");

        // 사용자 정보 설정 (공통)
        model.addAttribute("userName", "Linda Miller");
        model.addAttribute("userRole", "Front-end Developer");
        model.addAttribute("userAvatar", "/assets/admin/images/default-avatar.png");

        // 활성 메뉴 설정
        model.addAttribute("activeMenu", "vote");

        return "admin/vote/index";
    }

    @GetMapping("/dashboard/analytics")
    public String analytics(Model model) {
        // 페이지 제목 설정
        model.addAttribute("pageTitle", "애널리틱스 - Spark Admin");

        // 사용자 정보 설정 (공통)
        model.addAttribute("userName", "Linda Miller");
        model.addAttribute("userRole", "Front-end Developer");
        model.addAttribute("userAvatar", "/assets/admin/images/default-avatar.png");

        // 활성 메뉴 설정
        model.addAttribute("activeMenu", "dashboards");
        model.addAttribute("activeSubmenu", "analytics");

        return "admin/dashboard/analytics";
    }
}


