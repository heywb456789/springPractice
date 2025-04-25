package com.tomato.naraclub.application.original.code;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.tomato.naraclub.application.board.entity.QBoardPost;
import com.tomato.naraclub.application.original.entity.QVideo;
import com.tomato.naraclub.common.interfaces.SearchTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.function.Function;

@Getter
@AllArgsConstructor
public enum VideoSearchType implements SearchTypeEnum {

  VIDEO_ID("비디오 ID", s -> QVideo.video.id.eq(Long.valueOf(s))),
  VIDEO_TITLE_CONTENT("제목/내용", s -> {
    String keyword = "%" + s.trim() + "%";
    return QVideo.video.title.likeIgnoreCase(keyword)
           .or(QVideo.video.description.likeIgnoreCase(keyword));
  });

  private final String key;
  private final Function<String, BooleanExpression> expression;
  }