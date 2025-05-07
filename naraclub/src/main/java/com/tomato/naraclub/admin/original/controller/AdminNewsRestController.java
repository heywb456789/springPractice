package com.tomato.naraclub.admin.original.controller;

import com.tomato.naraclub.admin.original.dto.NewsArticleRequest;
import com.tomato.naraclub.admin.original.dto.NewsArticleResponse;
import com.tomato.naraclub.admin.original.service.AdminNewsService;
import com.tomato.naraclub.admin.security.AdminUserDetails;
import com.tomato.naraclub.application.original.dto.VideoResponse;
import com.tomato.naraclub.application.original.dto.VideoUploadRequest;
import com.tomato.naraclub.common.dto.ResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.admin.original.controller
 * @fileName : AdminNewsRestController
 * @date : 2025-05-07
 * @description :
 * @AUTHOR : MinjaeKim
 */
@RestController
@RequestMapping("/admin/original/news")
@RequiredArgsConstructor
public class AdminNewsRestController {

    private final AdminNewsService adminNewsService;

    @PostMapping(value = "/upload",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseDTO<NewsArticleResponse> uploadNews(
        @ModelAttribute @Valid NewsArticleRequest req,
        @AuthenticationPrincipal AdminUserDetails user){
        return ResponseDTO.ok(adminNewsService.uploadNews(req, user));
    }

    @PutMapping(value = "/update/{newsId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseDTO<NewsArticleResponse> updateNews(
        @PathVariable("newsId") Long newsId,
        @ModelAttribute @Valid NewsArticleRequest req,
        @AuthenticationPrincipal AdminUserDetails user) {
        return ResponseDTO.ok(adminNewsService.updateNews(newsId, req, user));
    }
}
