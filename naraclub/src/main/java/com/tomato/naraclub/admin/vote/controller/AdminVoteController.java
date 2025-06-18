package com.tomato.naraclub.admin.vote.controller;

import com.tomato.naraclub.admin.security.AdminUserDetails;
import com.tomato.naraclub.admin.vote.service.AdminVoteService;
import com.tomato.naraclub.application.vote.dto.VoteListRequest;
import com.tomato.naraclub.application.vote.dto.VotePostResponse;
import com.tomato.naraclub.common.dto.ListDTO;
import com.tomato.naraclub.common.dto.Pagination;
import com.tomato.naraclub.common.util.DateUtils;
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
 * @packageName : com.tomato.naraclub.admin.vote.controller
 * @fileName : AdminVoteController
 * @date : 2025-04-29
 * @description :
 * @AUTHOR : MinjaeKim
 */
@Controller
@RequestMapping("/admin/vote")
@RequiredArgsConstructor
public class AdminVoteController {

    private final AdminVoteService adminVoteService;

    @GetMapping("/list")
    public String list(
        VoteListRequest request,
        @AuthenticationPrincipal AdminUserDetails user,
        @RequestParam(name = "page", defaultValue = "0") int page,
        @RequestParam(name = "size", defaultValue = "10") int size,
        Model model) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        ListDTO<VotePostResponse> votePage = adminVoteService.getVoteList(user, request, pageable);

        // ListDTO에서 페이징 정보 추출
        Pagination pagination = votePage.getPagination();
        List<VotePostResponse> voteList = votePage.getData();

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
        model.addAttribute("voteList", voteList);
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
        model.addAttribute("request", request);
        model.addAttribute("pageTitle", "투표 관리 - 나라걱정 클럽 관리자");

        // 사용자 정보 설정 (공통)
        model.addAttribute("userName", user.getUsername());
        model.addAttribute("userRole", user.getAuthorities());
        model.addAttribute("userRoleDisplay", user.getAdmin().getRole().getDisplayName());
        model.addAttribute("userAvatar", null);

        return "admin/vote/voteList";
    }

    @GetMapping("/{id}")
    public String voteDetail(Model model,
        @PathVariable long id,
        @AuthenticationPrincipal AdminUserDetails user) {

        VotePostResponse vote = adminVoteService.getVoteDetail(id);

        model.addAttribute("vote", vote);
        model.addAttribute("dayPercentage", DateUtils.progressMillsPercentage(vote.getStartDate(), vote.getEndDate()));

        // 사용자 정보 설정 (공통)
        model.addAttribute("userName", user.getUsername());
        model.addAttribute("userRole", user.getAuthorities());
        model.addAttribute("userRoleDisplay", user.getAdmin().getRole().getDisplayName());
        model.addAttribute("userAvatar", null);
        return "admin/vote/voteDetail";
    }

    @GetMapping("/create")
    public String create(Model model, @AuthenticationPrincipal AdminUserDetails user) {
        model.addAttribute("pageTitle", "투표 등록 - 나라걱정 클럽 관리자");
        model.addAttribute("userName", user.getUsername());
        model.addAttribute("userRole", user.getAuthorities());
        return "admin/vote/voteForm";
    }

    @GetMapping("/update/{id}")
    public String update(Model model, @PathVariable Long id, @AuthenticationPrincipal AdminUserDetails user) {

        VotePostResponse vote = adminVoteService.getVoteDetail(id);

        model.addAttribute("vote", vote);
        model.addAttribute("pageTitle", "투표 수정 - 나라걱정 클럽 관리자");

        // 사용자 정보 설정 (공통)
        model.addAttribute("userName", user.getUsername());
        model.addAttribute("userRole", user.getAuthorities());
        model.addAttribute("userRoleDisplay", user.getAdmin().getRole().getDisplayName());
        model.addAttribute("userAvatar", null);
        return "admin/vote/voteForm";
    }
}
