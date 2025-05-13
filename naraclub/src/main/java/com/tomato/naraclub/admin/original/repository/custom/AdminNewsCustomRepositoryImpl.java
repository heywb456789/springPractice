package com.tomato.naraclub.admin.original.repository.custom;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.QBean;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tomato.naraclub.admin.original.dto.NewsArticleResponse;
import com.tomato.naraclub.admin.original.dto.NewsListRequest;
import com.tomato.naraclub.admin.security.AdminUserDetails;
import com.tomato.naraclub.application.original.entity.QArticle;
import com.tomato.naraclub.common.dto.ListDTO;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
public class AdminNewsCustomRepositoryImpl implements AdminNewsCustomRepository{

    private final JPAQueryFactory query;

    @Override
    public ListDTO<NewsArticleResponse> getNewsList(NewsListRequest request, AdminUserDetails user, Pageable pageable) {
        QArticle qArticle = QArticle.article;

        if (request.getDateRange() != null && !request.getDateRange().isBlank()) {
            request.parseDateRange();
        }

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

        List<NewsArticleResponse> articles = query
            .select(getNewsFields(qArticle))
            .from(qArticle)
            .where(condition)
            .orderBy(request.getSortOrder())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        return ListDTO.of(countQuery, articles, pageable);
    }

    private QBean<NewsArticleResponse> getNewsFields(QArticle qArticle) {
        return Projections.fields(
            NewsArticleResponse.class,
            qArticle.id.as("articleId"),
            qArticle.title,
            qArticle.content,
            qArticle.type,
            qArticle.category,
            qArticle.thumbnailUrl,
            qArticle.viewCount,
            qArticle.commentCount,
            qArticle.publishedAt,
            qArticle.isPublic,
            qArticle.isHot,
            qArticle.author.name.as("authorName"),
            qArticle.createdAt,
            qArticle.updatedAt
        );
    }
}
