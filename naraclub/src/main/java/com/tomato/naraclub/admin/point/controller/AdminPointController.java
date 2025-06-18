package com.tomato.naraclub.admin.point.controller;

import com.tomato.naraclub.admin.original.dto.NewsArticleResponse;
import com.tomato.naraclub.admin.original.dto.NewsListRequest;
import com.tomato.naraclub.admin.point.dto.PointListRequest;
import com.tomato.naraclub.admin.point.dto.PointResponse;
import com.tomato.naraclub.admin.point.service.AdminPointService;
import com.tomato.naraclub.admin.security.AdminUserDetails;
import com.tomato.naraclub.application.member.entity.Member;
import com.tomato.naraclub.application.original.code.OriginalCategory;
import com.tomato.naraclub.application.original.code.OriginalType;
import com.tomato.naraclub.common.dto.ListDTO;
import com.tomato.naraclub.common.dto.Pagination;
import java.util.List;
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
 * @packageName : com.tomato.naraclub.admin.point.controller
 * @fileName : AdminPointController
 * @date : 2025-05-16
 * @description :
 * @AUTHOR : MinjaeKim
 */
@Controller
@RequestMapping("/admin/points")
@RequiredArgsConstructor
public class AdminPointController {

    private final AdminPointService adminPointService;

    @GetMapping("/list")
    public String pointsList(
        PointListRequest request,
        @AuthenticationPrincipal AdminUserDetails user,
        @RequestParam(name = "page", defaultValue = "0") int page,
        @RequestParam(name = "size", defaultValue = "10") int size,
        Model model) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));

        ListDTO<PointResponse> pointPage = adminPointService.getPointList(request, user, pageable);

        // ListDTO에서 페이징 정보 추출
        Pagination pagination = pointPage.getPagination();
        List<PointResponse> pointHistoryList = pointPage.getData();

        int totalPages = pagination.getTotalPages();
        int currentPage = pagination.getCurrentPage(); // 이미 1-based
        long totalCount = pagination.getTotalElements();

        // 페이지 범위 계산 (최대 10개 페이지 표시)
        int startPage = Math.max(1, currentPage - 5);
        int endPage = Math.min(startPage + 9, totalPages);

        // startPage 재조정 (끝 페이지 기준으로)
        if (endPage - startPage < 9 && startPage > 1) {
            startPage = Math.max(1, endPage - 9);
        }

        // 모델에 데이터 추가
        model.addAttribute("pointHistoryList", pointHistoryList);
        model.addAttribute("totalCount", totalCount);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("currentPage", currentPage); // 1-based
        model.addAttribute("currentPageZeroIndex", page); // 0-based (URL용)
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("pageSize", size);

        // 이전/다음 페이지 여부
        model.addAttribute("hasPrevious", page > 0);
        model.addAttribute("hasNext", page < totalPages - 1);

        // 기타 정보
        model.addAttribute("categories", OriginalCategory.values());
        model.addAttribute("types", OriginalType.getNewsArticleTypes());
        model.addAttribute("pageTitle", "Original 포인트 관리 - 나라걱정 클럽 관리자");
        model.addAttribute("searchRequest", request);

        // 사용자 정보 설정 (공통)
        model.addAttribute("userName", user.getUsername());
        model.addAttribute("userRole", user.getAuthorities());
        model.addAttribute("userRoleDisplay", user.getAdmin().getRole().getDisplayName());
        model.addAttribute("userAvatar", null);

        return "admin/point/pointList";
    }

    @GetMapping("/user/{id}")
    public String user(
        PointListRequest request,
        @PathVariable Long id,
        @AuthenticationPrincipal AdminUserDetails user,
        @RequestParam(name = "page", defaultValue = "0") int page,
        @RequestParam(name = "size", defaultValue = "50") int size,
        Model model) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        ListDTO<PointResponse> pointPage = adminPointService.getUserPointList(request, id, user, pageable);
        Member member = adminPointService.getMember(id);

        int totalPages = pointPage.getPagination().getTotalPages();
        int currentPage = pointPage.getPagination().getCurrentPage();
        int startPage = Math.max(1, currentPage);
        int endPage = Math.min(startPage + 9, totalPages);

        model.addAttribute("pointHistoryList", pointPage.getData());
        model.addAttribute("member", member);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("searchRequest", request);
        model.addAttribute("size", size);

        // 페이지 제목 및 활성 메뉴 설정
        model.addAttribute("categories", OriginalCategory.values());
        model.addAttribute("types", OriginalType.getNewsArticleTypes());
        model.addAttribute("pageTitle", "Original 포인트 관리 - 나라걱정 클럽 관리자");
        model.addAttribute("activeMenu", "points");
        model.addAttribute("activeSubmenu", "list");


        // 사용자 정보 설정 (공통)
        model.addAttribute("userName", user.getUsername());
        model.addAttribute("userRole", user.getAuthorities());
        model.addAttribute("userRoleDisplay", user.getAdmin().getRole().getDisplayName());
        model.addAttribute("userAvatar", null);
        return "admin/point/pointDetail";
    }

}
