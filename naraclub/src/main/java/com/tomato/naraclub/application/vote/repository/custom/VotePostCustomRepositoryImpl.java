package com.tomato.naraclub.application.vote.repository.custom;

import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.list;
import static com.querydsl.core.group.GroupBy.set;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.QBean;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tomato.naraclub.application.vote.code.VoteSortType;
import com.tomato.naraclub.application.vote.dto.VoteListRequest;
import com.tomato.naraclub.application.vote.dto.VoteOptionDTO;
import com.tomato.naraclub.application.vote.dto.VotePostResponse;
import com.tomato.naraclub.application.vote.entity.QVoteOption;
import com.tomato.naraclub.application.vote.entity.QVotePost;
import com.tomato.naraclub.common.dto.ListDTO;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.application.vote.repository.custom
 * @fileName : VotePostCustomRepositoryImpl
 * @date : 2025-04-23
 * @description :
 * @AUTHOR : MinjaeKim
 */
@RequiredArgsConstructor
public class VotePostCustomRepositoryImpl implements VotePostCustomRepository {

    private final JPAQueryFactory query;

    @Override
    public ListDTO<VotePostResponse> getList(VoteListRequest request, Pageable pageable) {
        QVotePost vote = QVotePost.votePost;
        QVoteOption voteOption = QVoteOption.voteOption;
        //1) 검색 기간
        Predicate condition = ExpressionUtils.allOf(
            getSearchCondition(request, vote, voteOption),
            request.isPeriod(vote.createdAt)
        );

        //2)countQuery
        JPAQuery<Long> countQuery = query.select(vote.count()).from(vote).where(condition);

        //3)리스트 데이터
        List<VotePostResponse> content = query
            .from(vote)
            .leftJoin(vote.voteOptions, voteOption)
            .where(condition)
            .orderBy(getSortOrder(request))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .transform(
                groupBy(vote.id).list(
                    Projections.bean(
                        VotePostResponse.class,
                        vote.id.as("votePostId"),
                        vote.author.id.as("authorId"),
                        vote.question,
                        list(
                            Projections.bean(
                                VoteOptionDTO.class,
                                voteOption.id.as("optionId"),
                                voteOption.optionName,
                                voteOption.voteCount
                            )
                        ).as("voteOptions"),
                        vote.commentCount,
                        vote.viewCount,
                        vote.isNew,
                        vote.createdAt,
                        vote.updatedAt
                    )
                )
            );

        return ListDTO.of(countQuery, content, pageable);
    }

    private QBean<VotePostResponse> getVotePostFields() {
        QVotePost votePost = QVotePost.votePost;
        QVoteOption voteOption = QVoteOption.voteOption;
        return Projections.fields(
            VotePostResponse.class,
            votePost.id.as("votePostId"),
            votePost.author.id.as("authorId"),
            votePost.question,
            votePost.commentCount.as("commentCount"),
            votePost.viewCount.as("viewCount"),
            votePost.isNew.as("isNew"),
            votePost.createdAt,
            votePost.updatedAt
//            JPAExpressions.selectOne()
//                .from(voteRecord)
//                .where(
//                    voteRecord.topic.id.eq(votePost.id),
//                    voteRecord.author.id.eq(authorId)
//                )
//                .exists().as("isVoted"),

        );
    }

    private OrderSpecifier<?> getSortOrder(VoteListRequest request) {
        return (request.getSortType() != null ? request.getSortType().getOrder()
            : VoteSortType.LATEST.getOrder());
    }

    private BooleanExpression getSearchCondition(VoteListRequest request, QVotePost vote,
        QVoteOption voteOption) {
        if (request == null || request.getSearchText() == null || request.getSearchText().trim()
            .isEmpty()) {
            return null;
        }
        String keyword = "%" + request.getSearchText() + "%";
        return vote.question.likeIgnoreCase(keyword)
            .or(voteOption.optionName.likeIgnoreCase(keyword));
    }
}
