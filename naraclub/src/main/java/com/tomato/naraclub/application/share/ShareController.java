package com.tomato.naraclub.application.share;

import com.tomato.naraclub.application.board.dto.ShareResponse;
import com.tomato.naraclub.application.board.service.BoardPostService;
import com.tomato.naraclub.application.original.service.NewsArticleService;
import com.tomato.naraclub.application.original.service.VideoService;
import com.tomato.naraclub.application.vote.service.VotePostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/share")
public class ShareController {

    public static final String VOTING_IMAGE = "https://image.newstomato.com/newstomato/club/share/voting.png";
    public static final String FREE_BOARD_IMAGE = "https://image.newstomato.com/newstomato/club/share/freeboard.png";
    private final BoardPostService boardPostService;
    private final VotePostService votePostService;
    private final NewsArticleService newsArticleService;
    private final VideoService videoService;

    @GetMapping("/board/{id}")
    public String shareBoard(@PathVariable Long id, Model model) {
        ShareResponse post = boardPostService.getShareInfo(id);
        String redirectUrl = post.getTitle().isEmpty() ? "/" : "/board/boardDetail.html?id=" + id;
        // Thymeleaf 템플릿에 바인딩할 OG 메타 정보
        model.addAttribute("metaTitle", post.getTitle());
        model.addAttribute("metaDesc", post.getSummary());
        model.addAttribute("metaImg", FREE_BOARD_IMAGE);
        model.addAttribute("redirectUrl", redirectUrl);
        model.addAttribute("id", id);
        return "share/boardDetail";
    }

    @GetMapping("/vote/{id}")
    public String shareVote(@PathVariable Long id, Model model) {
        ShareResponse post = votePostService.getShareInfo(id);
        String redirectUrl = post.getTitle().isEmpty() ? "/" : "/vote/voteDetail.html?id=" + id;
        // Thymeleaf 템플릿에 바인딩할 OG 메타 정보
        model.addAttribute("metaTitle", post.getTitle());
        model.addAttribute("metaDesc", post.getSummary());
        model.addAttribute("metaImg", VOTING_IMAGE);
        model.addAttribute("redirectUrl", redirectUrl);
        model.addAttribute("id", id);
        return "share/boardDetail";
    }

    @GetMapping("/news/{id}")
    public String shareNews(@PathVariable Long id, Model model) {
        ShareResponse post = newsArticleService.getShareInfo(id);
        String redirectUrl = post.getTitle().isEmpty() ? "/" : "/vote/voteDetail.html?id=" + id;
        // Thymeleaf 템플릿에 바인딩할 OG 메타 정보
        model.addAttribute("metaTitle", post.getTitle());
        model.addAttribute("metaDesc", post.getSummary());
        model.addAttribute("metaImg", post.getThumbnailUrl());
        model.addAttribute("redirectUrl", redirectUrl);
        model.addAttribute("id", id);
        return "share/boardDetail";
    }

    @GetMapping("/original/{id}")
    public String shareOriginal(@PathVariable Long id, Model model) {
        ShareResponse post = videoService.getShareInfo(id);
        String redirectUrl = post.getTitle().isEmpty() ? "/" : "/vote/voteDetail.html?id=" + id;
        // Thymeleaf 템플릿에 바인딩할 OG 메타 정보
        model.addAttribute("metaTitle", post.getTitle());
        model.addAttribute("metaDesc", post.getSummary());
        model.addAttribute("metaImg", post.getThumbnailUrl());
        model.addAttribute("redirectUrl", redirectUrl);
        model.addAttribute("id", id);
        return "share/boardDetail";
    }




}
