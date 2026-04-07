package com.mobe.mobe_life_backend.common.response;

import com.mobe.mobe_life_backend.common.constant.ErrorCode;

import lombok.Data;

@Data
public class Result<T> {

  private Integer code;

  private String message;

  private T data;

  public static <T> Result<T> success() {
    Result<T> result = new Result<>();
    result.setCode(ErrorCode.SUCCESS);
    result.setMessage("success");
    return result;
  }

  public static <T> Result<T> success(T data) {
    Result<T> result = new Result<>();
    result.setCode(ErrorCode.SUCCESS);
    result.setMessage("success");
    result.setData(data);
    return result;
  }

  public static <T> Result<T> error(Integer code, String message) {
    Result<T> result = new Result<>();
    result.setCode(code);
    result.setMessage(message);
    return result;
  }

  public Result<T> message(String message) {
    this.setMessage(message);
    return this;
  }

  public Result<T> data(T data) {
    this.setData(data);
    return this;
  }
}