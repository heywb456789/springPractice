package com.tomato.naraclub.admin.original.code;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.tomato.naraclub.application.original.entity.QArticle;
import com.tomato.naraclub.application.original.entity.QVideo;


public enum NewsSortType {
    LATEST   (QArticle.article.createdAt),
  PUBLISHED(QArticle.article.publishedAt),
  POPULAR  (QArticle.article.viewCount),
  COMMENT  (QArticle.article.commentCount),
  TITLE    (QArticle.article.title);

  private final Path<? extends Comparable<?>> path;

  NewsSortType(Path<? extends Comparable<?>> path) {
    this.path = path;
  }

  /** 주어진 방향으로 OrderSpecifier를 만들어 주는 헬퍼 */
  public OrderSpecifier<?> order(Order direction) {
    return new OrderSpecifier<>(direction, path);
  }
}
