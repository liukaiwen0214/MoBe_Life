/**
 * 核心职责：定义节点模块的错误码。
 * 所属业务模块：节点模块 / 错误码体系。
 * 重要依赖关系或外部约束：实现 BaseErrorCode 接口，确保错误码格式和属性的一致性。
 */
package com.mobe.mobe_life_backend.common.exception;

import org.springframework.http.HttpStatus;

/**
 * 节点模块错误码枚举。
 *
 * <p>包含与节点相关的错误码，如节点创建、修改、关联等错误。</p>
 */
public enum NodeErrorCode implements BaseErrorCode {

  /** 节点无法关联到已完成任务 */
  NODE_CANNOT_LINK_COMPLETED_TASK("NOD-2001", HttpStatus.BAD_REQUEST, "节点无法关联到已完成任务"),
  
  /** 节点所属对象不存在 */
  NODE_OWNER_NOT_FOUND("NOD-2002", HttpStatus.NOT_FOUND, "节点所属对象不存在"),
  
  /** 无权限访问该节点 */
  NO_PERMISSION_ACCESS_NODE("NOD-3001", HttpStatus.FORBIDDEN, "无权限访问该节点"),
  
  /** 节点不存在 */
  NODE_NOT_FOUND("NOD-4001", HttpStatus.NOT_FOUND, "节点不存在");

  private final String code;
  private final HttpStatus httpStatus;
  private final String defaultMessage;

  NodeErrorCode(String code, HttpStatus httpStatus, String defaultMessage) {
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