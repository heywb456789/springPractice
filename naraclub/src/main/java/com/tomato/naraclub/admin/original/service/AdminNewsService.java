package com.tomato.naraclub.admin.original.service;

import com.tomato.naraclub.admin.original.dto.NewsArticleRequest;
import com.tomato.naraclub.admin.original.dto.NewsArticleResponse;
import com.tomato.naraclub.admin.original.dto.NewsListRequest;
import com.tomato.naraclub.admin.security.AdminUserDetails;
import com.tomato.naraclub.application.original.entity.Article;
import com.tomato.naraclub.common.dto.ListDTO;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;

public interface AdminNewsService {
    ListDTO<NewsArticleResponse> getNewsList(NewsListRequest request, AdminUserDetails user, Pageable pageable);

    Article getArticleById(Long id);

    NewsArticleResponse uploadNews(@Valid NewsArticleRequest req, AdminUserDetails user);

    NewsArticleResponse updateNews(Long newsId, @Valid NewsArticleRequest req, AdminUserDetails user);
}
