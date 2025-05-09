package com.tomato.naraclub.application.board.code;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.tomato.naraclub.application.board.entity.QBoardPost;
import com.tomato.naraclub.common.interfaces.SearchTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.function.Function;

@Getter
@AllArgsConstructor
public enum BoardSearchType implements SearchTypeEnum {

  BOARD_ID("게시판 ID", s -> QBoardPost.boardPost.id.eq(Long.valueOf(s))),
  BOARD_TITLE_CONTENT("제목/내용", s -> {
    String keyword = "%" + s.trim() + "%";
    return QBoardPost.boardPost.title.likeIgnoreCase(keyword)
           .or(QBoardPost.boardPost.content.likeIgnoreCase(keyword));
  }),
  BOARD_TITLE("제목",  s -> {
    String keyword = "%" + s.trim() + "%";
    return QBoardPost.boardPost.title.likeIgnoreCase(keyword);
  }),
  BOARD_CONTENT("내용",  s -> {
    String keyword = "%" + s.trim() + "%";
    return QBoardPost.boardPost.content.likeIgnoreCase(keyword);
  }),
  BOARD_AUTHOR("작성자",  s -> {
    String keyword = "%" + s.trim() + "%";
    return QBoardPost.boardPost.author.name.likeIgnoreCase(keyword);
  })
  ;

  private final String key;
  private final Function<String, BooleanExpression> expression;
  }