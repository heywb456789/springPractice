package com.tomato.naraclub.application.original.repository.custom;

import com.tomato.naraclub.admin.original.dto.NewsArticleResponse;
import com.tomato.naraclub.admin.original.dto.NewsListRequest;
import com.tomato.naraclub.application.security.MemberUserDetails;
import com.tomato.naraclub.common.dto.ListDTO;
import org.springframework.data.domain.Pageable;

public interface NewsArticleCustomRepository {
    ListDTO<NewsArticleResponse> getNewsList(NewsListRequest request, MemberUserDetails userDetails, Pageable pageable);
}
