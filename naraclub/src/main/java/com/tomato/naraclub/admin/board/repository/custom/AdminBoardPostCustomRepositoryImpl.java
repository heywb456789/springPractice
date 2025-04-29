package com.tomato.naraclub.admin.board.repository.custom;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.QBean;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tomato.naraclub.admin.security.AdminUserDetails;
import com.tomato.naraclub.application.board.dto.BoardListRequest;
import com.tomato.naraclub.application.board.dto.BoardPostResponse;
import com.tomato.naraclub.application.board.entity.QBoardPost;
import com.tomato.naraclub.common.dto.ListDTO;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.admin.board.repository.custom
 * @fileName : AdminBoardPostCustomRepositoryImpl
 * @date : 2025-04-28
 * @description :
 * @AUTHOR : MinjaeKim
 */
@RequiredArgsConstructor
public class AdminBoardPostCustomRepositoryImpl implements AdminBoardPostCustomRepository {

    private final JPAQueryFactory query;

    @Override
    @Transactional(readOnly = true)
    public ListDTO<BoardPostResponse> getBoardList(AdminUserDetails user, BoardListRequest request,
        Pageable pageable) {
        QBoardPost board = QBoardPost.boardPost;


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

        // 3) contentQuery: 실제 페이지 데이터 조회
        List<BoardPostResponse> content = query
                .select(getBoardPostFields(board))
                .from(board)
                .where(condition)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(request.getSortOrder())
                .fetch();

        return ListDTO.of(countQuery, content, pageable);
    }

    private QBean<BoardPostResponse> getBoardPostFields(QBoardPost board) {
        return Projections.fields(
                BoardPostResponse.class,
                board.id           .as("boardId"),
                board.author.id    .as("authorId"),
                board.title,
                board.content,
                board.commentCount,
                board.views,
                board.shareCount   .as("sharesCount"),
                board.isHot,
                board.createdAt
        );
    }
}
