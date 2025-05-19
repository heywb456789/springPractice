package com.tomato.naraclub.application.comment.repository.custom;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.QBean;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tomato.naraclub.application.comment.dto.CommentResponse;
import com.tomato.naraclub.application.comment.entity.QArticleComments;
import com.tomato.naraclub.application.security.MemberUserDetails;
import com.tomato.naraclub.common.dto.ListDTO;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.application.comment.repository.custom
 * @fileName : ArticleCommentsCustomRepositoryImpl
 * @date : 2025-05-08
 * @description :
 * @AUTHOR : MinjaeKim
 */
@RequiredArgsConstructor
public class ArticleCommentsCustomRepositoryImpl implements ArticleCommentsCustomRepository {

    private final JPAQueryFactory query;

    @Override
    public ListDTO<CommentResponse> getNewsComments(Long newsId, MemberUserDetails user,
        Pageable pageable) {
        QArticleComments articleComments = QArticleComments.articleComments;

        Long userId = user != null ? user.getMember().getId() : 0L;

        BooleanExpression condition = articleComments.article.id.eq(newsId).and(articleComments.deleted.eq(false));

        // count 쿼리
        JPAQuery<Long> countQuery = query
            .select(articleComments.count())
            .from(articleComments)
            .where(condition);

        // content 쿼리 (최신순)
        List<CommentResponse> content = query
            .select(
                getNewsCommentFields(userId, articleComments)
            )
            .from(articleComments)
            .where(condition)
            .orderBy(articleComments.createdAt.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();
        return ListDTO.of(countQuery, content, pageable);
    }

    private QBean<CommentResponse> getNewsCommentFields(Long userId, QArticleComments articleComments) {
        return Projections.fields(
            CommentResponse.class,
            articleComments.id.as("commentId"),
            articleComments.author.id.as("authorId"),
            articleComments.author.name.as("authorName"),
            articleComments.author.id.eq(userId).as("isMine"),
            articleComments.content,
            articleComments.createdAt,
            articleComments.updatedAt
        );
    }
}
