package com.tomato.naraclub.application.original.code;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.tomato.naraclub.application.board.entity.QBoardPost;
import com.tomato.naraclub.application.original.entity.QVideo;
import lombok.Getter;

public enum VideoSortType {
  LATEST   (QVideo.video.createdAt),
  PUBLISHED(QVideo.video.publishedAt),
  POPULAR  (QVideo.video.viewCount),
  COMMENT  (QVideo.video.commentCount),
  TITLE    (QVideo.video.title);

  private final Path<? extends Comparable<?>> path;
  VideoSortType(Path<? extends Comparable<?>> path) {
    this.path = path;
  }

  /** 주어진 방향으로 OrderSpecifier를 만들어 주는 헬퍼 */
  public OrderSpecifier<?> order(Order direction) {
    return new OrderSpecifier<>(direction, path);
  }
}
