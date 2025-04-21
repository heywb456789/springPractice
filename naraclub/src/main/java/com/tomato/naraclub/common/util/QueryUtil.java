package com.tomato.naraclub.common.util;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Arrays;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class QueryUtil {

  private QueryUtil() {
    throw new IllegalStateException("Utility class");
  }


  public static OrderSpecifier<?>[] convertOrders(Sort sort) {
    return convertOrders(sort, null);
  }

  public static <T> OrderSpecifier<?>[] convertOrders(Sort sort, Class<T> responseClass) {
    if (sort.isUnsorted()) {
      return new OrderSpecifier[0];
    }
    if (responseClass == null) {
      return sort.toList().stream()
          .map(QueryUtil::convertOrder)
          .toArray(OrderSpecifier[]::new);
    }
    var fieldNames = Arrays.stream(responseClass.getDeclaredFields()).map(Field::getName).toList();
    return sort.toList().stream()
        .filter(order -> fieldNames.contains(order.getProperty()))
        .map(QueryUtil::convertOrder)
        .toArray(OrderSpecifier[]::new);
  }

  public static <T> JPAQuery<T> paging(JPAQuery<T> jpaQuery, Pageable pageable) {
    if (pageable.isUnpaged()) {
      return jpaQuery;
    }
    return jpaQuery
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize());
  }

  public static <T> JPAQuery<T> pagingAndOrdering(JPAQuery<T> jpaQuery, Pageable pageable) {
    return paging(jpaQuery, pageable).orderBy(convertOrders(pageable.getSort(), jpaQuery.getType()));
  }

  /**
   *
   * Spring Data Sort → QueryDSL OrderSpecifier 변환
   * responseClass가 주어지면 필드명 필터링해서 존재하는 필드만 사용
   * Expressions.stringPath(...)로 동적으로 QueryDSL Path 생성
   */
  private static OrderSpecifier<?> convertOrder(Sort.Order order) {
    var direction = order.isAscending() ? Order.ASC : Order.DESC;

    // 가볍게 타입 예외처리 → createdAt은 datePath, id는 numberPath
    String property = order.getProperty();
    if ("createdAt".equals(property)) {
      return new OrderSpecifier<>(direction, Expressions.dateTimePath(LocalDateTime.class, property));
    } else if ("id".equals(property)) {
      return new OrderSpecifier<>(direction, Expressions.numberPath(Long.class, property));
    }
    return new OrderSpecifier<>(direction, Expressions.stringPath(property));
  }

}
