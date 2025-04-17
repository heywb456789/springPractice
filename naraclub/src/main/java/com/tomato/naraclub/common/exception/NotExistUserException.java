package com.tomato.naraclub.common.exception;

import me.bread.common.code.ResponseStatus;

public class NotExistUserException extends APIException {

  public NotExistUserException() {
    super(ResponseStatus.NOT_EXIST_USER);
  }
}
