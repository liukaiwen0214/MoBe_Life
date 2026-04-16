/**
 * 核心职责：定义用户模块的错误码。
 * 所属业务模块：用户模块 / 错误码体系。
 * 重要依赖关系或外部约束：实现 BaseErrorCode 接口，确保错误码格式和属性的一致性。
 */
package com.mobe.mobe_life_backend.common.exception;

import org.springframework.http.HttpStatus;

/**
 * 用户模块错误码枚举。
 *
 * <p>包含与用户相关的错误码，如用户资料错误、权限错误等。</p>
 */
public enum UserErrorCode implements BaseErrorCode {

  /** 昵称长度不合法 */
  NICKNAME_LENGTH_INVALID("USR-1001", HttpStatus.BAD_REQUEST, "昵称长度不合法"),
  
  /** 不能解绑主联系方式 */
  CANNOT_UNBIND_PRIMARY_CONTACT("USR-2001", HttpStatus.BAD_REQUEST, "不能解绑主联系方式"),
  
  /** 至少保留一种联系方式 */
  AT_LEAST_ONE_CONTACT_REQUIRED("USR-2002", HttpStatus.BAD_REQUEST, "至少保留一种联系方式"),
  
  /** 无权修改他人资料 */
  NO_PERMISSION_MODIFY_OTHERS("USR-3001", HttpStatus.FORBIDDEN, "无权修改他人资料"),
  
  /** 用户资料不存在 */
  USER_PROFILE_NOT_FOUND("USR-4001", HttpStatus.NOT_FOUND, "用户资料不存在");

  private final String code;
  private final HttpStatus httpStatus;
  private final String defaultMessage;

  UserErrorCode(String code, HttpStatus httpStatus, String defaultMessage) {
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