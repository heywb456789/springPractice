package com.tomato.naraclub.common.code;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Arrays;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@AllArgsConstructor
public enum ResponseStatus {
  OK("OK_0000", "정상 처리되었습니다.", HttpStatus.OK),
  DATA_NOT_FOUND("ER_0001", "데이터를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  NO_CHANGE_DATA("ER_0002", "수정된 데이터가 없습니다.", HttpStatus.NO_CONTENT),
  BAD_REQUEST("ER_0003", "잘못된 요청입니다.", HttpStatus.BAD_REQUEST),
  UNPROCESSABLE_ENTITY("ER_9998", "처리할 수 없는 요청입니다.", HttpStatus.UNPROCESSABLE_ENTITY),
  INTERNAL_SERVER_ERROR("ER_9999", "서버 내부 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
  EXIST_USER("ER_1001", "이미 가입된 계정입니다.", HttpStatus.BAD_REQUEST),
  WITHDRAWAL_USER("ER_1002", "탈퇴한 회원입니다. 재가입 해주세요.", HttpStatus.UNAUTHORIZED),
  FORBIDDEN("ER_1003", "권한이 없는 사용자입니다. 고객센터에 문의 부탁드립니다.", HttpStatus.FORBIDDEN),
  UNAUTHORIZED("ER_1004", "로그인이 필요합니다.", HttpStatus.UNAUTHORIZED),
  UNAUTHORIZED_ID_PW("ER_1005", "아이디 비밀번호를 확인해주세요.", HttpStatus.UNAUTHORIZED),
  UNAUTHORIZED_ROLE("ER_1006", "계정이 활성화되어 있지 않거나 권한이 없습니다.", HttpStatus.UNAUTHORIZED),
  UNAUTHORIZED_ONE_ID("ER_1007", "원아이디 로그인 실패 ID / PW를 확인해주세요. ", HttpStatus.UNAUTHORIZED),
  ALREADY_MODIFIED_STATUS("ER_1008", "이미 변경된 상태 입니다.", HttpStatus.BAD_REQUEST),

  BOARD_POST_NOT_EXIST("ER_2001", "게시글이 없습니다. 잘못 된 요청입니다.", HttpStatus.INTERNAL_SERVER_ERROR),
  ALREADY_LIKED("ER_2002","이미 좋아요 처리가 되었습니다.", HttpStatus.INTERNAL_SERVER_ERROR ),
  ALREADY_DELETED_LIKE("ER_2003","좋아요 취소 처리가 이미 완료되었습니다.", HttpStatus.INTERNAL_SERVER_ERROR ),


  USER_NOT_EXIST("ER_3001", "회원 정보를 찾을수가 없습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
  USER_NOT_ACTIVE("ER_3002", "인증된 회원이 아닙니다.", HttpStatus.INTERNAL_SERVER_ERROR),

  VOTE_POST_NOT_EXIST("ER_4001", "투표 게시글이 없습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
  VOTE_POST_OPTIONS_NOT_EXIST("ER_4002", "투표 옵션이 없습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
  ALREADY_VOTED("ER_4003","이미 투표 처리가 완료되었습니다.", HttpStatus.INTERNAL_SERVER_ERROR ),

  VIDEO_NOT_EXIST("ER_5001","비디오가 존재하지 않습니다.", HttpStatus.INTERNAL_SERVER_ERROR ),

  ARTICLE_NOT_EXIST("ER_6001","비디오가 존재하지 않습니다.", HttpStatus.INTERNAL_SERVER_ERROR ),
  THUMBNAIL_IS_NECESSARY("ER_6002","썸네일은 필수 입니다.", HttpStatus.BAD_REQUEST ),

  ACTIVITY_NOT_EXIST("ER_7001","활동내역이 존재하지 않습니다.", HttpStatus.BAD_REQUEST ),
  ;
  @JsonProperty("code")
  private final String code;

  @JsonProperty("message")
  private final String message;

  private final HttpStatus httpStatus; // ⭐ 추가된 필드

  @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
  public static ResponseStatus forValues(
          @JsonProperty("code") String code,
          @JsonProperty("message") String message
  ) {
    return Arrays.stream(values())
            .filter(s -> s.code.equals(code))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Unknown code: " + code));
  }

  public static ResponseStatus of(String code) {
    return Arrays.stream(values())
            .filter(s -> s.code.equals(code))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Invalid code: " + code));
  }
}

