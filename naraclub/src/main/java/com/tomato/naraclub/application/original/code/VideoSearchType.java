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
  }),
  VIDEO_TITLE("제목", s -> {
    String keyword = "%" + s.trim() + "%";
    return QVideo.video.title.likeIgnoreCase(keyword);
  }),
  VIDEO_DESCRIPTION("설명", s -> {
    String keyword = "%" + s.trim() + "%";
    return QVideo.video.description.likeIgnoreCase(keyword);
  }),
  VIDEO_AUTHOR("등록자", s -> {
    String keyword = "%" + s.trim() + "%";
    return QVideo.video.author.name.likeIgnoreCase(keyword);
  }),
  ;

  private final String key;
  private final Function<String, BooleanExpression> expression;
  }