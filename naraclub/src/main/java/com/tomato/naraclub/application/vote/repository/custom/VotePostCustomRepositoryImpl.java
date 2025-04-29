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
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tomato.naraclub.application.vote.code.VoteSortType;
import com.tomato.naraclub.application.vote.dto.VoteListRequest;
import com.tomato.naraclub.application.vote.dto.VoteOptionDTO;
import com.tomato.naraclub.application.vote.dto.VotePostResponse;
import com.tomato.naraclub.application.vote.entity.QVoteOption;
import com.tomato.naraclub.application.vote.entity.QVotePost;
import com.tomato.naraclub.application.vote.entity.QVoteViewHistory;
import com.tomato.naraclub.common.dto.ListDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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
    public ListDTO<VotePostResponse> getList(Long memberId, VoteListRequest request, Pageable pageable) {
        QVotePost vote = QVotePost.votePost;
        QVoteOption voteOption = QVoteOption.voteOption;
        QVoteViewHistory viewHistory = QVoteViewHistory.voteViewHistory;

        LocalDateTime startOfToday    = LocalDate.now().atStartOfDay();
        LocalDateTime startOfTomorrow = startOfToday.plusDays(1);
        BooleanExpression createdToday = vote.createdAt.goe(startOfToday)
                .and(vote.createdAt.lt(startOfTomorrow));

        BooleanExpression hasNoHistory = JPAExpressions.selectOne()
                .from(viewHistory)
                .where(
                        viewHistory.reader.id.eq(memberId==null ? 0L : memberId),
                        viewHistory.votePost.id.eq(vote.id)
                )
                .exists()
                .not();
        //회원 비회원 분기
        BooleanExpression isNewExpr = (memberId != null)
                ? createdToday.and(hasNoHistory)
                : createdToday;


        // 1) 검색·기간 조건
        Predicate condition = ExpressionUtils.allOf(
                request.getSearchCondition(),
                request.isPeriod(vote.createdAt)
        );

        // 2) 전체 개수(countQuery)
        JPAQuery<Long> countQuery = query
                .select(vote.count())
                .from(vote)
                .where(condition);

        // 3) 페이징용 ID만 먼저 조회 (포스트별로 한 행만)
        List<Long> postIds = query
                .select(vote.id)
                .from(vote)
                .where(condition)
                .orderBy(request.getSortOrder())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        if (postIds.isEmpty()) {
            // 조회할 포스트가 없으면 빈 리스트 반환
            return ListDTO.of(countQuery, Collections.emptyList(), pageable);
        }

        // 4) 조회된 ID 기준으로 옵션(join) 포함 전체 데이터 재조회
        List<VotePostResponse> content = query
                .from(vote)
                .leftJoin(vote.voteOptions, voteOption)
                .where(vote.id.in(postIds))
                .orderBy(request.getSortOrder())
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
                                                        voteOption.voteCount,
                                                        voteOption.createdAt,
                                                        voteOption.updatedAt
                                                )
                                        ).as("voteOptions"),
                                        vote.commentCount,
                                        vote.viewCount,
                                        isNewExpr.as("new"),
                                        vote.createdAt,
                                        vote.updatedAt
                                )
                        )
                );

        // 5) 3)에서 가져온 순서(postIds)대로 정렬
        Map<Long, VotePostResponse> mapById = content.stream()
                .collect(Collectors.toMap(VotePostResponse::getVotePostId, Function.identity()));

        List<VotePostResponse> sorted = postIds.stream()
                .map(mapById::get)
                .collect(Collectors.toList());

        // 6) 최종 반환
        return ListDTO.of(countQuery, sorted, pageable);
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


}
