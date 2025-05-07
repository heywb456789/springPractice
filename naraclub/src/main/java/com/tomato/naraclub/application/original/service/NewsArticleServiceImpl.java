package com.tomato.naraclub.application.original.service;

import com.tomato.naraclub.admin.original.dto.NewsArticleResponse;
import com.tomato.naraclub.admin.original.dto.NewsListRequest;
import com.tomato.naraclub.application.original.repository.NewsArticleRepository;
import com.tomato.naraclub.application.security.MemberUserDetails;
import com.tomato.naraclub.common.dto.ListDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class NewsArticleServiceImpl implements NewsArticleService{

    private final NewsArticleRepository newsArticleRepository;

    @Override
    public ListDTO<NewsArticleResponse> getNewsList(NewsListRequest request, MemberUserDetails userDetails, Pageable pageable) {
        return newsArticleRepository.getNewsList(request, userDetails, pageable);
    }
}
