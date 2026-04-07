package com.mobe.mobe_life_backend.common.constant;

public final class ErrorCode {

  private ErrorCode() {
  }

  /**
   * 成功
   */
  public static final int SUCCESS = 0;

  /**
   * 参数错误
   */
  public static final int PARAMS_ERROR = 40000;

  /**
   * 未登录
   */
  public static final int NOT_LOGIN_ERROR = 40100;

  /**
   * 无权限
   */
  public static final int NO_AUTH_ERROR = 40300;

  /**
   * 资源不存在
   */
  public static final int NOT_FOUND_ERROR = 40400;

  /**
   * 系统错误
   */
  public static final int SYSTEM_ERROR = 50000;
}