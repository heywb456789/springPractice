package com.tomato.naraclub.common.exception;

import me.bread.common.code.ResponseStatus;

public class ForbiddenException extends RuntimeException {

  public ForbiddenException() {
    super(ResponseStatus.FORBIDDEN.getMessage());
  }
}
