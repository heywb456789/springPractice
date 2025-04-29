package com.tomato.naraclub.admin.vote.controller;

import com.tomato.naraclub.admin.security.AdminUserDetails;
import com.tomato.naraclub.admin.vote.service.AdminVoteService;
import com.tomato.naraclub.application.vote.dto.VoteListRequest;
import com.tomato.naraclub.application.vote.dto.VotePostResponse;
import com.tomato.naraclub.common.dto.ListDTO;
import com.tomato.naraclub.common.util.DateUtils;
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

        int totalPages = votePage.getPagination().getTotalPages();
        int currentPage = votePage.getPagination().getCurrentPage();
        int startPage = Math.max(1, currentPage);
        int endPage = Math.min(startPage + 9, totalPages);

        model.addAttribute("voteList", votePage.getData());
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        // 페이지 제목 및 활성 메뉴 설정
        model.addAttribute("pageTitle", "투표 관리 - 나라사랑 클럽 관리자");
        model.addAttribute("activeMenu", "board");

        // 사용자 정보 설정 (공통)
        model.addAttribute("userName", user.getUsername());
        model.addAttribute("userRole", user.getAuthorities());
        return "admin/vote/voteList";
    }

    @GetMapping("/{id}")
    public String voteDetail(Model model,
        @PathVariable long id,
        @AuthenticationPrincipal AdminUserDetails user) {

        VotePostResponse vote = adminVoteService.getVoteDetail(id);

        model.addAttribute("vote", vote);
        model.addAttribute("dayPercentage", DateUtils.progressDaysPercentage(vote.getStartDate(), vote.getEndDate()));

        // 사용자 정보 설정 (공통)
        model.addAttribute("userName", user.getUsername());
        model.addAttribute("userRole", user.getAuthorities());
        return "admin/vote/voteDetail";
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("pageTitle", "투표 등록 - 나라사랑 클럽 관리자");
        return "admin/vote/voteForm";
    }

    @GetMapping("/update/{id}")
    public String update(Model model, @PathVariable Long id) {

        VotePostResponse vote = adminVoteService.getVoteDetail(id);

        model.addAttribute("vote", vote);
        model.addAttribute("pageTitle", "투표 수정 - 나라사랑 클럽 관리자");
        return "admin/vote/voteForm";
    }
}
