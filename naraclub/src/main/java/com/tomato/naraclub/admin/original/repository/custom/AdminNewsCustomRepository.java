package com.tomato.naraclub.admin.original.repository.custom;

import com.tomato.naraclub.admin.original.dto.NewsArticleResponse;
import com.tomato.naraclub.admin.original.dto.NewsListRequest;
import com.tomato.naraclub.admin.security.AdminUserDetails;
import com.tomato.naraclub.common.dto.ListDTO;
import org.springframework.data.domain.Pageable;

public interface AdminNewsCustomRepository {
    ListDTO<NewsArticleResponse> getNewsList(NewsListRequest request, AdminUserDetails user, Pageable pageable);
}
