package com.tomato.naraclub.admin.board.controller;
import com.tomato.naraclub.admin.board.dto.AdminBoardDto;
import com.tomato.naraclub.admin.board.service.AdminBoardService;
import com.tomato.naraclub.admin.security.AdminUserDetails;
import com.tomato.naraclub.application.board.dto.BoardListRequest;
import com.tomato.naraclub.application.board.dto.BoardPostResponse;
import com.tomato.naraclub.application.security.MemberUserDetails;
import com.tomato.naraclub.common.dto.ListDTO;
import com.tomato.naraclub.common.dto.Pagination;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/board")
@RequiredArgsConstructor
public class AdminBoardController {

    private final AdminBoardService adminBoardService;

    /**
     * 게시글 목록 페이지
     */
    @GetMapping("/list")
    public String list(
            BoardListRequest request,
            @AuthenticationPrincipal AdminUserDetails user,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            Model model) {

        // 게시글 목록 조회
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "boardId"));

        ListDTO<BoardPostResponse> boardPage = adminBoardService.getBoardList(user, request, pageable);

        // ListDTO에서 페이징 정보 추출
        Pagination pagination = boardPage.getPagination();
        List<BoardPostResponse> boardList = boardPage.getData();

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
        model.addAttribute("boardList", boardList);
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
        model.addAttribute("pageTitle", "게시글 관리 - NaraSarang Admin");

        // 사용자 정보 설정 (공통)
        model.addAttribute("userName", user.getUsername());
        model.addAttribute("userRole", user.getAuthorities());
        model.addAttribute("userRoleDisplay", user.getAdmin().getRole().getDisplayName());
        model.addAttribute("userAvatar", null);

        return "admin/board/boardList";
    }

    /**
     * 게시글 상세 보기
     */
    @GetMapping("/{id}")
    public String getBoardDetail(
        @AuthenticationPrincipal AdminUserDetails user,
        @PathVariable("id") Long id,
        Model model) {
        AdminBoardDto board = adminBoardService.getBoardDetail(id);
        model.addAttribute("board", board);

        // 페이지 제목 및 활성 메뉴 설정
        model.addAttribute("pageTitle", board.getTitle());
        model.addAttribute("activeMenu", "board");

        // 사용자 정보 설정 (공통)
        model.addAttribute("userName", user.getUsername());
        model.addAttribute("userRole", user.getAuthorities());
        model.addAttribute("userRoleDisplay", user.getAdmin().getRole().getDisplayName());
        model.addAttribute("userAvatar", null);

        return "admin/board/boardDetail";
    }
//
//    /**
//     * 게시글 작성 페이지
//     */
//    @GetMapping("/create")
//    public String createForm(Model model) {
//        model.addAttribute("board", new BoardDto());
//
//        // 페이지 제목 및 활성 메뉴 설정
//        model.addAttribute("pageTitle", "게시글 등록 - 나라걱정 관리자");
//        model.addAttribute("activeMenu", "board");
//
//        // 사용자 정보 설정 (공통)
//        model.addAttribute("userName", "Linda Miller");
//        model.addAttribute("userRole", "Front-end Developer");
//        model.addAttribute("userAvatar", "/assets/admin/images/default-avatar.png");
//
//        return "admin/boardForm";
//    }
//
//    /**
//     * 게시글 저장
//     */
//    @PostMapping("/create")
//    public String create(@ModelAttribute BoardDto boardDto, RedirectAttributes redirectAttributes) {
//        Long boardId = adminBoardService.saveBoard(boardDto);
//        redirectAttributes.addFlashAttribute("message", "게시글이 등록되었습니다.");
//        return "redirect:/admin/board/view/" + boardId;
//    }
//
//    /**
//     * 게시글 수정 페이지
//     */
//    @GetMapping("/edit/{id}")
//    public String editForm(@PathVariable("id") Long id, Model model) {
//        BoardDto board = adminBoardService.getBoard(id);
//        model.addAttribute("board", board);
//
//        // 페이지 제목 및 활성 메뉴 설정
//        model.addAttribute("pageTitle", "게시글 수정 - 나라걱정 관리자");
//        model.addAttribute("activeMenu", "board");
//
//        // 사용자 정보 설정 (공통)
//        model.addAttribute("userName", "Linda Miller");
//        model.addAttribute("userRole", "Front-end Developer");
//        model.addAttribute("userAvatar", "/assets/admin/images/default-avatar.png");
//
//        return "admin/boardForm";
//    }
//
//    /**
//     * 게시글 수정
//     */
//    @PostMapping("/edit/{id}")
//    public String edit(
//            @PathVariable("id") Long id,
//            @ModelAttribute BoardDto boardDto,
//            RedirectAttributes redirectAttributes) {
//        boardDto.setId(id);
//        adminBoardService.saveBoard(boardDto);
//        redirectAttributes.addFlashAttribute("message", "게시글이 수정되었습니다.");
//        return "redirect:/admin/board/view/" + id;
//    }
//
//    /**
//     * 게시글 삭제 (AJAX)
//     */
//    @PostMapping("/delete")
//    @ResponseBody
//    public Map<String, Object> delete(@RequestBody Map<String, List<Long>> requestData) {
//        Map<String, Object> result = new HashMap<>();
//
//        try {
//            List<Long> ids = requestData.get("ids");
//            adminBoardService.deleteBoards(ids);
//            result.put("success", true);
//            result.put("message", "게시글이 삭제되었습니다.");
//        } catch (Exception e) {
//            result.put("success", false);
//            result.put("message", "게시글 삭제 중 오류가 발생했습니다.");
//        }
//
//        return result;
//    }
}