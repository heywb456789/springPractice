package com.tomato.naraclub.admin.original.service;

import com.tomato.naraclub.admin.original.dto.NewsArticleResponse;
import com.tomato.naraclub.admin.original.dto.NewsListRequest;
import com.tomato.naraclub.admin.security.AdminUserDetails;
import com.tomato.naraclub.common.dto.ListDTO;
import org.springframework.data.domain.Pageable;

public interface AdminNewsService {
    ListDTO<NewsArticleResponse> getNewsList(NewsListRequest request, AdminUserDetails user, Pageable pageable);
}
