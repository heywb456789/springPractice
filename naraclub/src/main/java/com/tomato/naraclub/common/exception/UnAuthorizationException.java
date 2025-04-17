package com.tomato.naraclub.common.exception;

import me.bread.common.code.ResponseStatus;

public class UnAuthorizationException extends RuntimeException {

  public UnAuthorizationException() {
    super(ResponseStatus.UNAUTHORIZED.getMessage());
  }
}
