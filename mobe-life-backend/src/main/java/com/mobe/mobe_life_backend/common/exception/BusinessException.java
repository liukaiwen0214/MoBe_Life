package com.mobe.mobe_life_backend.common.exception;

import com.mobe.mobe_life_backend.common.constant.ErrorCode;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

  private final Integer code;

  public BusinessException(String message) {
    super(message);
    this.code = ErrorCode.PARAMS_ERROR;
  }

  public BusinessException(Integer code, String message) {
    super(message);
    this.code = code;
  }
}