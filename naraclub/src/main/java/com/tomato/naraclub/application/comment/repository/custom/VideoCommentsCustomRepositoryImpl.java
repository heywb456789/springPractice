package com.tomato.naraclub.application.comment.repository.custom;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.QBean;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tomato.naraclub.application.comment.dto.CommentResponse;
import com.tomato.naraclub.application.comment.entity.QVideoComments;
import com.tomato.naraclub.application.security.MemberUserDetails;
import com.tomato.naraclub.common.dto.ListDTO;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.application.comment.repository.custom
 * @fileName : VideoCommentsCustomRepositoryImpl
 * @date : 2025-04-24
 * @description :
 * @AUTHOR : MinjaeKim
 */
@RequiredArgsConstructor
public class VideoCommentsCustomRepositoryImpl implements VideoCommentsCustomRepository {

    private final JPAQueryFactory query;

    @Override
    public ListDTO<CommentResponse> getVideoComments(Long videoId, MemberUserDetails user,
        Pageable pageable) {
        QVideoComments qVideoComments = QVideoComments.videoComments;

        Long userId = user == null ? 0L : user.getMember().getId();

        BooleanExpression condition = qVideoComments.video.id.eq(videoId);

        // count 쿼리
        JPAQuery<Long> countQuery = query
            .select(qVideoComments.count())
            .from(qVideoComments)
            .where(condition);

        // content 쿼리 (최신순)
        List<CommentResponse> content = query
            .select(
                getVoteCommentFields(userId)
            )
            .from(qVideoComments)
            .where(condition)
            .orderBy(qVideoComments.createdAt.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        return ListDTO.of(countQuery, content, pageable);
    }

    private QBean<CommentResponse> getVoteCommentFields(Long userId) {
        QVideoComments qVideoComments = QVideoComments.videoComments;
        return Projections.fields(
            CommentResponse.class,
            qVideoComments.id.as("commentId"),
            qVideoComments.author.id.as("authorId"),
            qVideoComments.author.name.as("authorName"),
            qVideoComments.author.id.eq(userId).as("isMine"),
            qVideoComments.content,
            qVideoComments.createdAt,
            qVideoComments.updatedAt
        );
    }
}
