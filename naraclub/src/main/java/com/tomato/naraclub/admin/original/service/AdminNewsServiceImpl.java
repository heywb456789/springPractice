package com.tomato.naraclub.admin.original.service;

import com.tomato.naraclub.admin.original.dto.NewsArticleResponse;
import com.tomato.naraclub.admin.original.dto.NewsListRequest;
import com.tomato.naraclub.admin.security.AdminUserDetails;
import com.tomato.naraclub.common.dto.ListDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminNewsServiceImpl implements AdminNewsService{

    @Override
    public ListDTO<NewsArticleResponse> getNewsList(NewsListRequest request, AdminUserDetails user, Pageable pageable) {
        return null;
    }
}
