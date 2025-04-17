package com.tomato.naraclub.common.exception;


import com.tomato.naraclub.common.code.ResponseStatus;

public class BadRequestException extends APIException {

  public BadRequestException() {
    super(ResponseStatus.BAD_REQUEST);
  }
}
