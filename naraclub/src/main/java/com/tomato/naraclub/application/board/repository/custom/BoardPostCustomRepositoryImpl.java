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
                request.isPeriod(board.createdAt)
        );

        // 2) countQuery: 전체 건수 조회
        JPAQuery<Long> countQuery = query
                .select(board.count())
                .from(board)
                .where(condition);

        // 3) contentQuery: 실제 페이지 데이터 조회
        JPAQuery<BoardPostResponse> contentQuery = query
                .select(getBoardPostFields(board, viewHistory, memberId))
                .from(board);

        // 회원일 때만 VIEW_HISTORY 조인
        if (memberId != null) {
            contentQuery.leftJoin(viewHistory)
                    .on(viewHistory.reader.id.eq(memberId),
                            viewHistory.boardPost.id.eq(board.id));
        }

        List<BoardPostResponse> content = contentQuery
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

        LocalDateTime startOfToday    = LocalDate.now().atStartOfDay();
        LocalDateTime startOfTomorrow = startOfToday.plusDays(1);

        BooleanExpression createdToday = board.createdAt.goe(startOfToday)
                .and(board.createdAt.lt(startOfTomorrow));
        BooleanExpression noHistory    = vh.id.isNull();

        // isNew 식만 분기
        BooleanExpression isNewExpr = (memberId == null
                ? createdToday
                : createdToday.and(noHistory)
        );

        return Projections.fields(
                BoardPostResponse.class,
                board.id           .as("boardId"),
                board.author.id    .as("authorId"),
                board.title,
                board.content,
                board.commentCount,
                board.views,
                board.shareCount   .as("sharesCount"),
                isNewExpr.as("isNew"),
                board.isHot,
                board.createdAt
        );
    }


}
