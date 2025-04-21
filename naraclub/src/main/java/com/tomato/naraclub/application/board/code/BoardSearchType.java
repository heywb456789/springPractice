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

  BOARD_ID("정산 ID", s -> QBoardPost.boardPost.id.eq(Long.valueOf(s)))
//  WORK_TITLE("작품 타이틀", QSettlement.settlement.workTitle::eq),
//  WORK_ID("작품 ID", QSettlement.settlement.workId::eq),
//  CONTRACT_ID("계약 ID", QSettlement.settlement.contractId::eq),
//  CONTRACT_NAME("계약명", QSettlement.settlement.contractName::eq),
//  ACCOUNT_OWNER("계좌 소유주", QSettlement.settlement.accountOwner::like),
  ;

  private final String key;
  private final Function<String, BooleanExpression> expression;
  }