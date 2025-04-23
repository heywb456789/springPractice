package com.tomato.naraclub.application.vote.code;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.tomato.naraclub.application.board.entity.QBoardPost;
import com.tomato.naraclub.application.vote.entity.QVotePost;
import lombok.Getter;

@Getter
public enum VoteSortType {

    LATEST(new OrderSpecifier<>(Order.DESC, QVotePost.votePost.createdAt)),
    POPULAR(new OrderSpecifier<>(Order.DESC, QVotePost.votePost.viewCount)),
    SHARED(new OrderSpecifier<>(Order.DESC, QVotePost.votePost.shareCount));

    private final OrderSpecifier<?> order;

    VoteSortType(OrderSpecifier<?> order) {
        this.order = order;
    }
}
