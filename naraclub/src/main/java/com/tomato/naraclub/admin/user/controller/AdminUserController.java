package com.tomato.naraclub.admin.user.controller;

import com.tomato.naraclub.admin.security.AdminUserDetails;
import com.tomato.naraclub.admin.user.dto.AdminUserListRequest;
import com.tomato.naraclub.admin.user.dto.AdminUserResponse;
import com.tomato.naraclub.admin.user.dto.AppUserListRequest;
import com.tomato.naraclub.admin.user.dto.AppUserResponse;
import com.tomato.naraclub.admin.user.service.AdminUserService;
import com.tomato.naraclub.admin.user.service.AppUserService;
import com.tomato.naraclub.application.board.dto.BoardListRequest;
import com.tomato.naraclub.application.board.dto.BoardPostResponse;
import com.tomato.naraclub.common.dto.ListDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.admin.user.controller
 * @fileName : AdminUserController
 * @date : 2025-05-12
 * @description :
 * @AUTHOR : MinjaeKim
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/users")
public class AdminUserController {

    private final AppUserService appUserService;
    private final AdminUserService adminUserService;

    @GetMapping("/app/user-list")
    public String userList(
            AppUserListRequest request,
            @AuthenticationPrincipal AdminUserDetails user,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            Model model) {
        // 게시글 목록 조회
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        ListDTO<AppUserResponse> appUserList = appUserService.getAppUserList(user, request, pageable);

        // 페이징 정보
        int totalPages = appUserList.getPagination().getTotalPages();
        int currentPage = appUserList.getPagination().getCurrentPage();
        int startPage = Math.max(1, currentPage - 4);
        int endPage = Math.min(startPage + 9, totalPages);

        //모델에 데이터 추가
        model.addAttribute("userList", appUserList.getData());
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        // 페이지 제목 및 활성 메뉴 설정
        model.addAttribute("searchRequest", request);
        model.addAttribute("pageTitle", "앱 유저관리 - NaraSarang Admin");
        model.addAttribute("activeMenu", "users");
        model.addAttribute("activeSubmenu", "app");

        // 사용자 정보 설정 (공통)
        model.addAttribute("userName", user.getUsername());
        model.addAttribute("userRole", user.getAuthorities());
//        model.addAttribute("userAvatar", "/assets/admin/images/default-avatar.png");

        return "admin/user/appUserList";
    }

    @GetMapping("/app/user/{id}")
    public String userDetail(
        @AuthenticationPrincipal AdminUserDetails user,
        @PathVariable long id,
        Model model){
        AppUserResponse userResponse = appUserService.getAppUserDetail(id);

        model.addAttribute("user", userResponse);

        model.addAttribute("pageTitle", "앱 유저관리 - NaraSarang Admin");
        model.addAttribute("activeMenu", "users");
        model.addAttribute("activeSubmenu", "app");

        // 사용자 정보 설정 (공통)
        model.addAttribute("userName", user.getUsername());
        model.addAttribute("userRole", user.getAuthorities());

        return "admin/user/appUserDetail";
    }

    @GetMapping("/admin/user-list")
    public String adminUserList(
            AdminUserListRequest request,
            @AuthenticationPrincipal AdminUserDetails user,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            Model model) {
        // 게시글 목록 조회
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        ListDTO<AdminUserResponse> adminUserList = adminUserService.getAdminUserList(user, request, pageable);

        // 페이징 정보
        int totalPages = adminUserList.getPagination().getTotalPages();
        int currentPage = adminUserList.getPagination().getCurrentPage();
        int startPage = Math.max(1, currentPage - 4);
        int endPage = Math.min(startPage + 9, totalPages);

        //모델에 데이터 추가
        model.addAttribute("userList", adminUserList.getData());
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        // 페이지 제목 및 활성 메뉴 설정
        model.addAttribute("searchRequest", request);
        model.addAttribute("pageTitle", "앱 유저관리 - NaraSarang Admin");
        model.addAttribute("activeMenu", "users");
        model.addAttribute("activeSubmenu", "admin");

        // 사용자 정보 설정 (공통)
        model.addAttribute("userName", user.getUsername());
        model.addAttribute("userRole", user.getAuthorities());
        model.addAttribute("userAvatar", "/assets/admin/images/default-avatar.png");

        return "admin/user/appUserList";
    }
}
