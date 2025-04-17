package com.tomato.naraclub.common.exception;

import me.bread.common.code.ResponseStatus;

public class NotFoundDataException extends APIException {

  public NotFoundDataException() {
    super(ResponseStatus.DATA_NOT_FOUND);
  }
}
