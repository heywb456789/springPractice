package com.tomato.naraclub.admin.activity.code;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.tomato.naraclub.application.member.entity.QMemberActivity;

public enum ActivitySortType {
    LATEST(QMemberActivity.memberActivity.createdAt),
    MEMBER_NAME(QMemberActivity.memberActivity.author.name),
    ;

    private final Path<? extends Comparable<?>> path;

    ActivitySortType(Path<? extends Comparable<?>> path) {
        this.path = path;
    }

    /**
     * 주어진 방향으로 OrderSpecifier를 만들어 주는 헬퍼
     */
    public OrderSpecifier<?> order(Order direction) {
        return new OrderSpecifier<>(direction, path);
    }
}
