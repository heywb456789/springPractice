package com.tomato.naraclub.application.original.code;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.tomato.naraclub.application.board.entity.QBoardPost;
import com.tomato.naraclub.application.original.entity.QVideo;
import lombok.Getter;

@Getter
public enum VideoSortType {

    LATEST(new OrderSpecifier<>(Order.DESC, QVideo.video.createdAt)),
    POPULAR(new OrderSpecifier<>(Order.DESC, QVideo.video.viewCount)),
//    SHARED(new OrderSpecifier<>(Order.DESC, QVideo.video.))
    ;

    private final OrderSpecifier<?> order;

    VideoSortType(OrderSpecifier<?> order) {
        this.order = order;
    }
}
