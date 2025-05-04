package com.tomato.naraclub.admin.original.controller;

import com.tomato.naraclub.admin.original.dto.NewsListRequest;
import com.tomato.naraclub.admin.original.service.AdminNewsService;
import com.tomato.naraclub.admin.security.AdminUserDetails;
import com.tomato.naraclub.application.original.code.OriginalCategory;
import com.tomato.naraclub.application.original.code.OriginalType;
import com.tomato.naraclub.application.original.dto.VideoResponse;
import com.tomato.naraclub.common.dto.ListDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/original/news")
public class AdminNewsController {
    private final AdminNewsService adminNewsService;

    @GetMapping("/list")
    public String newsList(
            NewsListRequest request,
            @AuthenticationPrincipal AdminUserDetails user,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            Model model){
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        ListDTO<NewsResponse>

        int totalPages = videoPage.getPagination().getTotalPages();
        int currentPage = videoPage.getPagination().getCurrentPage();
        int startPage = Math.max(1, currentPage);
        int endPage = Math.min(startPage + 9, totalPages);

        model.addAttribute("newsList", videoPage.getData());
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        // 페이지 제목 및 활성 메뉴 설정
        model.addAttribute("categories", OriginalCategory.values());
        model.addAttribute("types", OriginalType.getVideoTypes());
        model.addAttribute("pageTitle", "Original Video 관리 - 나라사랑 클럽 관리자");
        model.addAttribute("activeMenu", "board");
        model.addAttribute("request", request);

        // 사용자 정보 설정 (공통)
        model.addAttribute("userName", user.getUsername());
        model.addAttribute("userRole", user.getAuthorities());
        return "admin/news/newsList";
    }
}
