/**
 * 核心职责：定义认证模块的错误码。
 * 所属业务模块：认证模块 / 错误码体系。
 * 重要依赖关系或外部约束：实现 BaseErrorCode 接口，确保错误码格式和属性的一致性。
 */
package com.mobe.mobe_life_backend.common.exception;

import org.springframework.http.HttpStatus;

/**
 * 认证模块错误码枚举。
 *
 * <p>包含与认证相关的错误码，如登录失败、令牌过期等。</p>
 */
public enum AuthErrorCode implements BaseErrorCode {

  /** 账号或密码错误 */
  ACCOUNT_PASSWORD_ERROR("AUT-1001", HttpStatus.BAD_REQUEST, "账号或密码错误"),
  
  /** 验证码错误 */
  VERIFICATION_CODE_ERROR("AUT-1002", HttpStatus.BAD_REQUEST, "验证码错误"),
  
  /** 验证码已过期 */
  VERIFICATION_CODE_EXPIRED("AUT-1003", HttpStatus.BAD_REQUEST, "验证码已过期"),
  
  /** 不支持的登录类型 */
  UNSUPPORTED_LOGIN_TYPE("AUT-1004", HttpStatus.BAD_REQUEST, "不支持的登录类型"),
  
  /** 邮箱已注册 */
  EMAIL_ALREADY_REGISTERED("AUT-2001", HttpStatus.CONFLICT, "邮箱已注册"),
  
  /** 手机号已注册 */
  PHONE_ALREADY_REGISTERED("AUT-2002", HttpStatus.CONFLICT, "手机号已注册"),
  
  /** 邮箱已绑定其他账号 */
  EMAIL_BINDED_OTHER_ACCOUNT("AUT-2003", HttpStatus.CONFLICT, "邮箱已绑定其他账号"),
  
  /** 手机号已绑定其他账号 */
  PHONE_BINDED_OTHER_ACCOUNT("AUT-2004", HttpStatus.CONFLICT, "手机号已绑定其他账号"),
  
  /** 令牌已过期 */
  TOKEN_EXPIRED("AUT-3001", HttpStatus.UNAUTHORIZED, "令牌已过期"),
  
  /** 无效令牌 */
  INVALID_TOKEN("AUT-3002", HttpStatus.UNAUTHORIZED, "无效令牌"),
  
  /** 缺少认证令牌 */
  TOKEN_MISSING("AUT-3003", HttpStatus.UNAUTHORIZED, "缺少认证令牌"),
  
  /** 无权限访问 */
  NO_PERMISSION("AUT-3004", HttpStatus.FORBIDDEN, "无权限访问"),
  
  /** 用户不存在 */
  USER_NOT_FOUND("AUT-4001", HttpStatus.NOT_FOUND, "用户不存在"),
  
  /** 微信服务异常 */
  WECHAT_SERVICE_EXCEPTION("AUT-9001", HttpStatus.SERVICE_UNAVAILABLE, "微信服务异常");

  private final String code;
  private final HttpStatus httpStatus;
  private final String defaultMessage;

  AuthErrorCode(String code, HttpStatus httpStatus, String defaultMessage) {
    this.code = code;
    this.httpStatus = httpStatus;
    this.defaultMessage = defaultMessage;
  }

  @Override
  public String getCode() {
    return code;
  }

  @Override
  public HttpStatus getHttpStatus() {
    return httpStatus;
  }

  @Override
  public String getDefaultMessage() {
    return defaultMessage;
  }

}