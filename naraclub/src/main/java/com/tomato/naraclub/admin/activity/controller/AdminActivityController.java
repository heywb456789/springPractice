package com.tomato.naraclub.admin.activity.controller;

import com.tomato.naraclub.admin.activity.dto.ActivityListRequest;
import com.tomato.naraclub.admin.activity.service.AdminActivityService;
import com.tomato.naraclub.admin.point.dto.PointResponse;
import com.tomato.naraclub.admin.security.AdminUserDetails;
import com.tomato.naraclub.application.member.dto.MemberActivityResponse;
import com.tomato.naraclub.application.original.code.OriginalCategory;
import com.tomato.naraclub.application.original.code.OriginalType;
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
@RequestMapping("/admin/activity")
@RequiredArgsConstructor
public class AdminActivityController {

    private final AdminActivityService activityService;


    @GetMapping("/list")
    public String activityList(
        ActivityListRequest request,
        @AuthenticationPrincipal AdminUserDetails user,
        @RequestParam(name = "page", defaultValue = "0") int page,
        @RequestParam(name = "size", defaultValue = "10") int size,
        Model model) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        ListDTO<MemberActivityResponse> activityList = activityService.getActivityList(request, user, pageable);

        int totalPages = activityList.getPagination().getTotalPages();
        int currentPage = activityList.getPagination().getCurrentPage();
        int startPage = Math.max(1, currentPage);
        int endPage = Math.min(startPage + 9, totalPages);

        model.addAttribute("activityList", activityList.getData());
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        // 페이지 제목 및 활성 메뉴 설정
        model.addAttribute("categories", OriginalCategory.values());
        model.addAttribute("types", OriginalType.getNewsArticleTypes());
        model.addAttribute("pageTitle", "Original 포인트 관리 - 나라걱정 클럽 관리자");
        model.addAttribute("activeMenu", "points");
        model.addAttribute("activeSubmenu", "activity");
        model.addAttribute("searchRequest", request);


        // 사용자 정보 설정 (공통)
        model.addAttribute("userName", user.getUsername());
        model.addAttribute("userRole", user.getAuthorities());
        model.addAttribute("userRoleDisplay", user.getAdmin().getRole().getDisplayName());
        model.addAttribute("userAvatar", null);

        //custom 정보

        return "admin/activity/activityList";
    }


}
