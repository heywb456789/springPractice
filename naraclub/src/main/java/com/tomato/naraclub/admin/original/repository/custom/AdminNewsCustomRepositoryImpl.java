package com.tomato.naraclub.admin.original.repository.custom;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tomato.naraclub.admin.original.dto.NewsArticleResponse;
import com.tomato.naraclub.admin.original.dto.NewsListRequest;
import com.tomato.naraclub.admin.security.AdminUserDetails;
import com.tomato.naraclub.common.dto.ListDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
public class AdminNewsCustomRepositoryImpl implements AdminNewsCustomRepository{

    private final JPAQueryFactory query;

    @Override
    public ListDTO<NewsArticleResponse> getNewsList(NewsListRequest request, AdminUserDetails user, Pageable pageable) {
        return null;
    }
}
