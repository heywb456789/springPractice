package com.tomato.naraclub.application.board.repository.custom;

import com.querydsl.core.types.*;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tomato.naraclub.application.board.dto.BoardListRequest;
import com.tomato.naraclub.application.board.dto.BoardPostResponse;
import com.tomato.naraclub.application.board.entity.QBoardPost;
import com.tomato.naraclub.application.board.entity.QBoardPostViewHistory;
import com.tomato.naraclub.common.dto.ListDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
public class BoardPostCustomRepositoryImpl implements BoardPostCustomRepository {

    private final JPAQueryFactory query;

    @Override
    @Transactional(readOnly = true)
    public ListDTO<BoardPostResponse> getBoardPostList(Long memberId, BoardListRequest request,
        Pageable pageable) {
        QBoardPost board = QBoardPost.boardPost;
        QBoardPostViewHistory viewHistory = QBoardPostViewHistory.boardPostViewHistory;

        // 1) 검색·기간 조건을 하나의 BooleanExpression 으로 결합
        // Predicate 로 선언
        Predicate condition = ExpressionUtils.allOf(
            request.getSearchCondition(),
            request.isPeriod(board.createdAt),
            request.getIsNotDeleted()
        );

        // 2) countQuery: 전체 건수 조회
        JPAQuery<Long> countQuery = query
            .select(board.count())
            .from(board)
            .where(condition);

        List<BoardPostResponse> content = query
            .select(getBoardPostFields(board, viewHistory, memberId))
            .from(board)
            .where(condition)
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .orderBy(request.getSortOrder())
            .fetch();

        // 4) DTO 변환
        return ListDTO.of(countQuery, content, pageable);
    }

    private QBean<BoardPostResponse> getBoardPostFields(
        QBoardPost board,
        QBoardPostViewHistory vh,
        Long memberId) {

        LocalDateTime startOfToday = LocalDate.now().atStartOfDay();
        LocalDateTime startOfTomorrow = startOfToday.plusDays(1);

        BooleanExpression createdToday = board.createdAt.goe(startOfToday)
            .and(board.createdAt.lt(startOfTomorrow));

        // EXISTS 서브쿼리: 해당 회원의 조회 기록이 있는지
        BooleanExpression hasHistory = JPAExpressions
            .selectOne()
            .from(vh)
            .where(
                vh.reader.id.eq(memberId),
                vh.boardPost.id.eq(board.id)
            )
            .exists();

        // 새 글 여부 = 오늘 작성된 글이면서, 기록이 없는 경우
        BooleanExpression isNewExpr = (memberId == null
            ? createdToday
            : createdToday.and(hasHistory.not())
        );

        return Projections.fields(
            BoardPostResponse.class,
            board.id.as("boardId"),
            board.author.id.as("authorId"),
            board.title,
            board.content,
            board.commentCount,
            board.views,
            board.shareCount.as("sharesCount"),
            isNewExpr.as("isNew"),
            board.isHot,
            board.createdAt
        );
    }


}
