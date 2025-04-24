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
  BOARD_TITLE_CONTENT("제목/내용", s -> {
    String keyword = "%" + s.trim() + "%";
    return QVideo.video.title.likeIgnoreCase(keyword)
           .or(QVideo.video.description.likeIgnoreCase(keyword));
  });
//  WORK_ID("작품 ID", QSettlement.settlement.workId::eq),
//  CONTRACT_ID("계약 ID", QSettlement.settlement.contractId::eq),
//  CONTRACT_NAME("계약명", QSettlement.settlement.contractName::eq),
//  ACCOUNT_OWNER("계좌 소유주", QSettlement.settlement.accountOwner::like),
  ;

  private final String key;
  private final Function<String, BooleanExpression> expression;
  }