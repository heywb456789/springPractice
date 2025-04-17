package com.tomato.naraclub.common.exception;

import me.bread.common.code.ResponseStatus;

public class BadRequestException extends APIException {

  public BadRequestException() {
    super(ResponseStatus.BAD_REQUEST);
  }
}
