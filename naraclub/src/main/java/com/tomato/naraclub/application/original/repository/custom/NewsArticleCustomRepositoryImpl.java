package com.tomato.naraclub.application.original.repository.custom;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.QBean;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tomato.naraclub.admin.original.dto.NewsArticleResponse;
import com.tomato.naraclub.admin.original.dto.NewsListRequest;
import com.tomato.naraclub.application.original.entity.QArticle;
import com.tomato.naraclub.application.original.entity.QArticleViewHistory;
import com.tomato.naraclub.application.security.MemberUserDetails;
import com.tomato.naraclub.common.dto.ListDTO;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RequiredArgsConstructor
public class NewsArticleCustomRepositoryImpl implements NewsArticleCustomRepository{

    private final JPAQueryFactory query ;

    @Override
    public ListDTO<NewsArticleResponse> getNewsList(NewsListRequest request, MemberUserDetails userDetails, Pageable pageable) {
        QArticle qArticle = QArticle.article;
        QArticleViewHistory viewHistory = QArticleViewHistory.articleViewHistory;

        Long memberId = Optional.ofNullable(userDetails)
                .map(ud -> ud.getMember().getId())
                .orElse(null);


        Predicate condition = ExpressionUtils.allOf(
                request.getSearchCondition(),
                request.isPublishedAfter(qArticle.publishedAt),
                request.getOriginalTypeCondition(qArticle),
                request.getOriginalCategoryCondition(qArticle),
                request.isPeriod(qArticle.createdAt),
                request.isNotDeleted(),
                request.getPublishStatus(qArticle)
        );

        JPAQuery<Long> countQuery = query
                .select(qArticle.count())
                .from(qArticle)
                .where(condition);

        JPAQuery<NewsArticleResponse> select = query
                .select(getNewsFields(memberId, qArticle, viewHistory))
                .from(qArticle);

        if(memberId != null) {
            select.leftJoin(viewHistory)
                .on(
                    viewHistory.reader.id.eq(memberId),
                    viewHistory.article.id.eq(qArticle.id)
                );
        }

        List<NewsArticleResponse> articles = select
                .where(condition)
                .orderBy(request.getSortOrder())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return ListDTO.of(countQuery, articles, pageable);
    }

    private QBean<NewsArticleResponse> getNewsFields(Long memberId, QArticle qArticle,
        QArticleViewHistory viewHistory) {

        LocalDateTime startOfToday = LocalDate.now().atStartOfDay();
        LocalDateTime endOfToday = LocalDateTime.now().plusDays(1);

        BooleanExpression createdToday = qArticle
            .publishedAt.goe(startOfToday)
            .and(qArticle.publishedAt.lt(endOfToday));

        BooleanExpression noHistory = viewHistory.id.isNull();

        BooleanExpression isNewExpr = (memberId == null ? createdToday
                                                        : createdToday.and(noHistory));

        return Projections.fields(
                NewsArticleResponse.class,
                qArticle.id.as("articleId"),
                qArticle.title,
                qArticle.subTitle,
                qArticle.content,
                qArticle.type,
                qArticle.category,
                qArticle.thumbnailUrl,
                qArticle.viewCount,
                qArticle.commentCount,
                qArticle.publishedAt,
                qArticle.isPublic,
                qArticle.isHot,
                isNewExpr.as("isNew"),
                qArticle.author.name.as("authorName"),
                qArticle.createdAt,
                qArticle.updatedAt
        );
    }
}
