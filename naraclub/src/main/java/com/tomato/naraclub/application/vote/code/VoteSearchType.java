package com.tomato.naraclub.application.vote.code;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.tomato.naraclub.application.board.entity.QBoardPost;
import com.tomato.naraclub.application.vote.entity.QVotePost;
import com.tomato.naraclub.common.interfaces.SearchTypeEnum;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum VoteSearchType implements SearchTypeEnum {

  VOTE_ID("투표 ID", s -> QVotePost.votePost.id.eq(Long.valueOf(s))),
  VOTE_QUESTION("제목/내용", s -> {
    String keyword = "%" + s.trim() + "%";
    return QVotePost.votePost.question.likeIgnoreCase(keyword);
  });
  ;

  private final String key;
  private final Function<String, BooleanExpression> expression;
  }