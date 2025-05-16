package com.tomato.naraclub.admin.point.code;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.tomato.naraclub.application.original.entity.QArticle;
import com.tomato.naraclub.application.point.entity.QPointHistory;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.admin.point.code
 * @fileName : PointSortType
 * @date : 2025-05-16
 * @description :
 * @AUTHOR : MinjaeKim
 */
public enum PointSortType {
    LATEST(QPointHistory.pointHistory.createdAt),
    ;

    private final Path<? extends Comparable<?>> path;

    PointSortType(Path<? extends Comparable<?>> path) {
        this.path = path;
    }

    /**
     * 주어진 방향으로 OrderSpecifier를 만들어 주는 헬퍼
     */
    public OrderSpecifier<?> order(Order direction) {
        return new OrderSpecifier<>(direction, path);
    }
}
