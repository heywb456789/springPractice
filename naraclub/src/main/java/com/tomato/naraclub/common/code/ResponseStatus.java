package com.tomato.naraclub.common.code;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.Arrays;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ResponseStatus {
  OK                ("OK_0000", "정상 처리되었습니다."),
  DATA_NOT_FOUND    ("ER_0001", "데이터를 찾을 수 없습니다."),
  NO_CHANGE_DATA    ("ER_0002", "수정된 데이터가 없습니다."),
  BAD_REQUEST       ("ER_0003", "잘못된 요청입니다."),
  UNPROCESSABLE_ENTITY("ER_9998","오류가 발생했습니다."),
  INTERNAL_SERVER_ERROR("ER_9999","오류가 발생했습니다."),
  EXIST_USER        ("ER_1001", "계정 연결이 불가능합니다. 이미 해당 계정으로 회원 가입되었습니다."),
  WITHDRAWAL_USER   ("ER_1002", "탈퇴한 회원입니다.\n회원 가입을 다시 진행해주세요."),
  FORBIDDEN         ("ER_1003", "권한이 없는 사용자입니다.");

  @JsonProperty("code")
  private final String code;

  @JsonProperty("message")
  private final String message;

  // -- Remove Lombok’s @AllArgsConstructor and write your own private ctor
  private ResponseStatus(String code, String message) {
    this.code    = code;
    this.message = message;
  }

  /**
   * Jackson will now deserialize {"code":"…","message":"…"}
   * by calling this factory, *not* the enum’s constructor.
   */
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

  /**
   * Convenience method when you only have the code.
   */
  public static ResponseStatus of(String code) {
    return Arrays.stream(values())
            .filter(s -> s.code.equals(code))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Invalid code: " + code));
  }
}
