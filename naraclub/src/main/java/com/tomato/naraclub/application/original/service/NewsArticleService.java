package com.tomato.naraclub.application.original.service;

import com.tomato.naraclub.admin.original.dto.NewsArticleResponse;
import com.tomato.naraclub.admin.original.dto.NewsListRequest;
import com.tomato.naraclub.application.board.dto.ShareResponse;
import com.tomato.naraclub.application.security.MemberUserDetails;
import com.tomato.naraclub.common.dto.ListDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Pageable;

public interface NewsArticleService {
    ListDTO<NewsArticleResponse> getNewsList(NewsListRequest request, MemberUserDetails userDetails, Pageable pageable);

    NewsArticleResponse getNewsDetail(Long id, MemberUserDetails userDetails, HttpServletRequest request);

    ShareResponse getShareInfo(Long id);
}
