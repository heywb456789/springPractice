package com.tomato.naraclub.application.original.controller;

import com.tomato.naraclub.admin.original.dto.NewsArticleResponse;
import com.tomato.naraclub.admin.original.dto.NewsListRequest;
import com.tomato.naraclub.application.original.service.NewsArticleService;
import com.tomato.naraclub.application.security.MemberUserDetails;
import com.tomato.naraclub.common.dto.ListDTO;
import com.tomato.naraclub.common.dto.ResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/news")
@RequiredArgsConstructor
public class NewsArticleController {

    private final NewsArticleService newsArticleService;

    @GetMapping
    public ResponseDTO<ListDTO<NewsArticleResponse>> getNewsList(
            NewsListRequest request,
            @AuthenticationPrincipal MemberUserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC,"publishedAt"));
        return ResponseDTO.ok(newsArticleService.getNewsList(request, userDetails, pageable));
    }

    @GetMapping("/{id}")
    public ResponseDTO<NewsArticleResponse> getNewsArticle(
        @PathVariable Long id,
        @AuthenticationPrincipal MemberUserDetails userDetails,
        HttpServletRequest request){
        return ResponseDTO.ok(newsArticleService.getNewsDetail(id, userDetails, request));
    }
}
