package com.tomato.naraclub.admin.vote.repository.custom;

import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.list;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tomato.naraclub.admin.security.AdminUserDetails;
import com.tomato.naraclub.application.vote.dto.VoteListRequest;
import com.tomato.naraclub.application.vote.dto.VoteOptionDTO;
import com.tomato.naraclub.application.vote.dto.VotePostResponse;
import com.tomato.naraclub.application.vote.entity.QVoteOption;
import com.tomato.naraclub.application.vote.entity.QVotePost;
import com.tomato.naraclub.application.vote.entity.QVoteViewHistory;
import com.tomato.naraclub.common.dto.ListDTO;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.admin.vote.repository.custom
 * @fileName : AdminVoteCustomRepositoryImpl
 * @date : 2025-04-29
 * @description :
 * @AUTHOR : MinjaeKim
 */
@RequiredArgsConstructor
public class AdminVoteCustomRepositoryImpl implements AdminVoteCustomRepository {

    private final JPAQueryFactory query;

    @Override
    public ListDTO<VotePostResponse> getVoteList(AdminUserDetails user, VoteListRequest request,
        Pageable pageable) {

        QVotePost vote = QVotePost.votePost;
        QVoteOption voteOption = QVoteOption.voteOption;

        Predicate condition = ExpressionUtils.allOf(
                request.getSearchCondition(),
                request.isPeriod(vote.createdAt),
                request.isNotDeleted()
        );

        JPAQuery<Long> countQuery = query
                .select(vote.count())
                .from(vote)
                .where(condition);

        // 3) 페이징용 ID만 먼저 조회 (포스트별로 한 행만)
//        List<Long> postIds = query
//                .select(vote.id)
//                .from(vote)
//                .where(condition)
//                .orderBy(request.getSortOrder())
//                .offset(pageable.getOffset())
//                .limit(pageable.getPageSize())
//                .fetch();
//
//        if (postIds.isEmpty()) {
//            // 조회할 포스트가 없으면 빈 리스트 반환
//            return ListDTO.of(countQuery, Collections.emptyList(), pageable);
//        }

        // 4) 조회된 ID 기준으로 옵션(join) 포함 전체 데이터 재조회
        List<VotePostResponse> content = query
                .from(vote)
                .leftJoin(vote.voteOptions, voteOption)
                .where(condition)
                .orderBy(request.getSortOrder())
                .transform(
                        groupBy(vote.id).list(
                                Projections.bean(
                                        VotePostResponse.class,
                                        vote.id.as("votePostId"),
                                        vote.author.id.as("authorId"),
                                        vote.author.name.as("authorName"),
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
                                        vote.voteCount,
                                        vote.createdAt,
                                        vote.updatedAt,
                                        vote.startDate,
                                        vote.endDate
                                )
                        )
                );


        return ListDTO.of(countQuery, content, pageable);
    }
}
