package com.tomato.naraclub.application.board.code;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.tomato.naraclub.application.board.entity.QBoardPost;
import lombok.Getter;

@Getter
public enum BoardSortType {

    LATEST(new OrderSpecifier<>(Order.DESC, QBoardPost.boardPost.createdAt)),
    POPULAR(new OrderSpecifier<>(Order.DESC, QBoardPost.boardPost.views)),
    SHARED(new OrderSpecifier<>(Order.DESC, QBoardPost.boardPost.shareCount));

    private final OrderSpecifier<?> order;

    BoardSortType(OrderSpecifier<?> order) {
        this.order = order;
    }
}
