package com.tomato.naraclub.application.board.repository.custom;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.QBean;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tomato.naraclub.application.board.code.BoardSortType;
import com.tomato.naraclub.application.board.dto.BoardListRequest;
import com.tomato.naraclub.application.board.dto.BoardPostResponse;
import com.tomato.naraclub.application.board.entity.QBoardPost;
import com.tomato.naraclub.common.dto.ListDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class BoardPostCustomRepositoryImpl implements BoardPostCustomRepository {

    private final JPAQueryFactory query;

    @Override
    public ListDTO<BoardPostResponse> getBoardPostList(BoardListRequest request, Pageable pageable) {
        var board = QBoardPost.boardPost;

        // 목록 쿼리
        var listQuery = query.select(getBoardPostFields())
                .from(board)
                .where(
                        getSearchCondition(request, board),
                        request.isPeriod(board.createdAt)
                )
                .orderBy(getSortOrder(request)); // ✅ Enum 정렬 적용

        List<BoardPostResponse> list = listQuery
                .offset(pageable.getOffset()) // ✅ 페이징 직접 적용
                .limit(pageable.getPageSize())
                .fetch();

        // count 쿼리
        long total = Optional.ofNullable(
                query.select(board.count())
                        .from(board)
                        .where(
                                getSearchCondition(request, board),
                                request.isPeriod(board.createdAt)
                        )
                        .fetchOne()
        ).orElse(0L);


        return ListDTO.of(total, list);
    }

    private QBean<BoardPostResponse> getBoardPostFields() {
        var board = QBoardPost.boardPost;

        return Projections.fields(BoardPostResponse.class,
                board.id.as("boardId"),
                board.author.id.as("authorId"),
                board.title.as("title"),
                board.content.as("content"),
                board.commentCount.as("commentCount"),
                board.views.as("views"),
                board.shareCount.as("sharesCount"),
                board.isNew.as("isNew"),
                board.isHot.as("isHot"),
                board.createdAt.as("createdAt")
        );
    }

    private BooleanExpression getSearchCondition(BoardListRequest request, QBoardPost board) {
        if (request == null || request.getSearchText() == null || request.getSearchText().isBlank()) {
            return null;
        }

        String keyword = "%" + request.getSearchText().trim() + "%";
        return board.title.likeIgnoreCase(keyword)
                .or(board.content.likeIgnoreCase(keyword));
    }

    private com.querydsl.core.types.OrderSpecifier<?> getSortOrder(BoardListRequest request) {
        return request.getSortType() != null
                ? request.getSortType().getOrder()
                : BoardSortType.LATEST.getOrder(); // 기본 정렬
    }
}
