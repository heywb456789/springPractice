package com.tomato.naraclub.common.exception;

import com.tomato.naraclub.common.code.ResponseStatus;

public class UnAuthorizationException extends RuntimeException {

  public UnAuthorizationException() {
    super(ResponseStatus.UNAUTHORIZED.getMessage());
  }
}
