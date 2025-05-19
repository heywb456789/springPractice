package com.tomato.naraclub.application.comment.repository.custom;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.QBean;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tomato.naraclub.application.comment.dto.CommentResponse;
import com.tomato.naraclub.application.comment.entity.QVoteComments;
import com.tomato.naraclub.application.security.MemberUserDetails;
import com.tomato.naraclub.common.dto.ListDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RequiredArgsConstructor
public class VoteCommentsCustomRepositoryImpl implements VoteCommentsCustomRepository{

    private final JPAQueryFactory queryFactory;

    @Override
    public ListDTO<CommentResponse> getBoardPostsComments(Long votePostId, MemberUserDetails user, Pageable pageable) {
        QVoteComments qVote = QVoteComments.voteComments;
        Long userId = user==null ? 0L : user.getMember().getId();
        BooleanExpression condition = qVote.votePost.id.eq(votePostId).and(qVote.deleted.eq(false));

        // count 쿼리
        JPAQuery<Long> countQuery = queryFactory
                .select(qVote.count())
                .from(qVote)
                .where(condition);

        // content 쿼리 (최신순)
        List<CommentResponse> content = queryFactory
                .select(
                        getVoteCommentFields(userId)
                )
                .from(qVote)
                .where(condition)
                .orderBy(qVote.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return ListDTO.of(countQuery, content, pageable);
    }

    private QBean<CommentResponse> getVoteCommentFields(Long userId) {
        QVoteComments qVote = QVoteComments.voteComments;
        return Projections.fields(
                CommentResponse.class,
                qVote.id.as("commentId"),
                qVote.author.id.as("authorId"),
                qVote.author.name.as("authorName"),
                qVote.author.id.eq(userId).as("isMine"),
                qVote.content,
                qVote.createdAt,
                qVote.updatedAt
        );
    }
}
