package com.tomato.naraclub.application.original.service;

import com.tomato.naraclub.admin.original.dto.NewsArticleResponse;
import com.tomato.naraclub.admin.original.dto.NewsListRequest;
import com.tomato.naraclub.application.board.dto.ShareResponse;
import com.tomato.naraclub.application.member.entity.Member;
import com.tomato.naraclub.application.member.repository.MemberRepository;
import com.tomato.naraclub.application.original.entity.Article;
import com.tomato.naraclub.application.original.entity.ArticleViewHistory;
import com.tomato.naraclub.application.original.repository.NewsArticleRepository;
import com.tomato.naraclub.application.original.repository.NewsArticleViewHistoryRepository;
import com.tomato.naraclub.application.security.MemberUserDetails;
import com.tomato.naraclub.common.code.MemberStatus;
import com.tomato.naraclub.common.code.ResponseStatus;
import com.tomato.naraclub.common.dto.ListDTO;
import com.tomato.naraclub.common.exception.APIException;
import com.tomato.naraclub.common.util.UserDeviceInfoUtil;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class NewsArticleServiceImpl implements NewsArticleService{

    private final NewsArticleRepository newsArticleRepository;
    private final NewsArticleViewHistoryRepository articleViewHistoryRepository;
    private final MemberRepository memberRepository;

    @Override
    public ListDTO<NewsArticleResponse> getNewsList(NewsListRequest request, MemberUserDetails userDetails, Pageable pageable) {
        return newsArticleRepository.getNewsList(request, userDetails, pageable);
    }

    @Override
    @Transactional
    public NewsArticleResponse getNewsDetail(Long id, MemberUserDetails userDetails,
        HttpServletRequest request) {

        String ip = UserDeviceInfoUtil.getClientIp(request);
        String userAgent = UserDeviceInfoUtil.getUserAgent(request.getHeader("User-Agent"));
        String deviceType = UserDeviceInfoUtil.getDeviceType(userAgent);

        Optional<Member> optionalMember = Optional.ofNullable(userDetails)
            .flatMap(user -> memberRepository.findByIdAndStatus(user.getMember().getId(),
                MemberStatus.ACTIVE));

        Article article = newsArticleRepository.findPublishedArticle(id, LocalDateTime.now())
            .orElseThrow(()->new APIException(ResponseStatus.ARTICLE_NOT_EXIST));

        article.increaseViewCount();

        optionalMember.ifPresent(user -> articleViewHistoryRepository.save(
            ArticleViewHistory.builder()
                .reader(user)
                .article(article)
                .viewedAt(LocalDateTime.now())
                .ipAddress(ip)
                .userAgent(userAgent)
                .deviceType(deviceType)
                .build()
        ));

        return article.convertDTO();
    }

    @Override
    public ShareResponse getShareInfo(Long id) {
        Article article = newsArticleRepository.findByIdAndDeletedFalse(id)
                .orElse(new Article());

        return article.convertShareDTO();
    }
}
