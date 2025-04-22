package com.tomato.naraclub.application.comment.repository.custom;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.QBean;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tomato.naraclub.application.board.dto.BoardPostResponse;
import com.tomato.naraclub.application.board.entity.QBoardPost;
import com.tomato.naraclub.application.comment.dto.CommentResponse;
import com.tomato.naraclub.application.comment.entity.QBoardComments;
import com.tomato.naraclub.application.security.MemberUserDetails;
import com.tomato.naraclub.common.dto.ListDTO;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.application.comment.repository.custom
 * @fileName : BoardCommentsCustomRepositoryImpl
 * @date : 2025-04-22
 * @description :
 * @AUTHOR : MinjaeKim
 */
@RequiredArgsConstructor
public class BoardCommentsCustomRepositoryImpl implements BoardCommentsCustomRepository {

    private final JPAQueryFactory query;

    @Override
    @Transactional(readOnly = true)
    public ListDTO<CommentResponse> getBoardPostsComments(Long postId, MemberUserDetails user, Pageable pageable) {
        QBoardComments comment = QBoardComments.boardComments;
        Long userId = user==null ? 0L : user.getMember().getId();
        BooleanExpression condition = comment.boardPost.id.eq(postId);

        // count 쿼리
        JPAQuery<Long> countQuery = query
            .select(comment.count())
            .from(comment)
            .where(condition);

        // content 쿼리 (최신순)
        List<CommentResponse> content = query
            .select(
                getBoardCommentFields(userId)
            )
            .from(comment)
            .where(condition)
            .orderBy(comment.createdAt.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        return ListDTO.of(countQuery, content, pageable);
    }

    private QBean<CommentResponse> getBoardCommentFields(Long userId) {
        QBoardComments comment = QBoardComments.boardComments;
        return Projections.fields(
            CommentResponse.class,
            comment.id.as("commentId"),
            comment.author.id.as("authorId"),
            comment.author.name.as("authorName"),
            comment.author.id.eq(userId).as("isMine"),
            comment.content,
            comment.createdAt,
            comment.updatedAt
        );
    }
}
