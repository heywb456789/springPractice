package com.tomato.naraclub.admin.original.controller;

import com.tomato.naraclub.admin.original.dto.NewsListRequest;
import com.tomato.naraclub.admin.original.dto.NewsArticleResponse;
import com.tomato.naraclub.admin.original.service.AdminNewsService;
import com.tomato.naraclub.admin.security.AdminUserDetails;
import com.tomato.naraclub.application.comment.dto.CommentResponse;
import com.tomato.naraclub.application.original.code.OriginalCategory;
import com.tomato.naraclub.application.original.code.OriginalType;
import com.tomato.naraclub.application.original.entity.Article;
import com.tomato.naraclub.application.original.entity.Video;
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
        ListDTO<NewsArticleResponse> newsPage = adminNewsService.getNewsList(request, user, pageable);

        // ListDTO에서 페이징 정보 추출
        Pagination pagination = newsPage.getPagination();
        List<NewsArticleResponse> newsList = newsPage.getData();

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
        model.addAttribute("newsList", newsList);
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
        model.addAttribute("pageTitle", "Original News 관리 - 나라걱정 클럽 관리자");
        model.addAttribute("searchRequest", request);

        // 사용자 정보 설정 (공통)
        model.addAttribute("userName", user.getUsername());
        model.addAttribute("userRole", user.getAuthorities());
        model.addAttribute("userRoleDisplay", user.getAdmin().getRole().getDisplayName());
        model.addAttribute("userAvatar", null);

        return "admin/news/newsList";
    }

    @GetMapping("/{id}")
    public String newsDetail(
        @AuthenticationPrincipal AdminUserDetails user,
        @PathVariable("id") Long id,
        Model model){
        Article article = adminNewsService.getArticleById(id);
        model.addAttribute("news", article);
        // 사용자 정보 설정 (공통)
        model.addAttribute("userName", user.getUsername());
        model.addAttribute("userRole", user.getAuthorities());
        model.addAttribute("userRoleDisplay", user.getAdmin().getRole().getDisplayName());
        model.addAttribute("userAvatar", null);

        return "admin/news/newsDetail";
    }

    @GetMapping("/create")
    public String createArticle(Model model, @AuthenticationPrincipal AdminUserDetails user) {
        model.addAttribute("categories", OriginalCategory.values());
        model.addAttribute("types", OriginalType.getNewsArticleTypes());
        model.addAttribute("pageTitle", "Original News 등록 - 나라걱정 클럽 관리자");
        model.addAttribute("mode", "create");

        // 사용자 정보 설정 (공통)
        model.addAttribute("userName", user.getUsername());
        model.addAttribute("userRole", user.getAuthorities());
        model.addAttribute("userRoleDisplay", user.getAdmin().getRole().getDisplayName());
        model.addAttribute("userAvatar", null);
        return "admin/news/newsForm";
    }

    @GetMapping("/update/{id}")
    public String updateArticle(Model model,
        @PathVariable Long id,
        @AuthenticationPrincipal AdminUserDetails user) {

        Article article = adminNewsService.getArticleById(id);

        model.addAttribute("news", article);
        model.addAttribute("pageTitle", "Original News 수정 - 나라걱정 클럽 관리자");

        model.addAttribute("mode", "update");
        model.addAttribute("categories", OriginalCategory.values());
        model.addAttribute("types", OriginalType.getNewsArticleTypes());

        // 사용자 정보 설정 (공통)
        model.addAttribute("userName", user.getUsername());
        model.addAttribute("userRole", user.getAuthorities());
        model.addAttribute("userRoleDisplay", user.getAdmin().getRole().getDisplayName());
        model.addAttribute("userAvatar", null);
        return "admin/news/newsForm";
    }
}
