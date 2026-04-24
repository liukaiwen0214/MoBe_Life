/**
 * 核心职责：定义目标模块的错误码。
 * 所属业务模块：目标模块 / 错误码体系。
 * 重要依赖关系或外部约束：实现 BaseErrorCode 接口，确保错误码格式和属性的一致性。
 */
package com.mobe.mobe_life_backend.common.exception;

import org.springframework.http.HttpStatus;

/**
 * 目标模块错误码枚举。
 *
 * <p>包含与目标相关的错误码，如目标创建、修改、状态变更等错误。</p>
 */
public enum GoalErrorCode implements BaseErrorCode {

  /** 目标状态模板不存在 */
  GOAL_STATUS_TEMPLATE_NOT_FOUND("GOL-2001", HttpStatus.NOT_FOUND, "目标状态模板不存在"),
  
  /** 目标当前状态不存在 */
  GOAL_CURRENT_STATUS_NOT_FOUND("GOL-2002", HttpStatus.BAD_REQUEST, "目标当前状态不存在"),
  
  /** 无权限操作该目标 */
  NO_PERMISSION_OPERATE_GOAL("GOL-3001", HttpStatus.FORBIDDEN, "无权限操作该目标"),
  
  /** 目标不存在 */
  GOAL_NOT_FOUND("GOL-4001", HttpStatus.NOT_FOUND, "目标不存在");

  private final String code;
  private final HttpStatus httpStatus;
  private final String defaultMessage;

  GoalErrorCode(String code, HttpStatus httpStatus, String defaultMessage) {
    this.code = code;
    this.httpStatus = httpStatus;
    this.defaultMessage = defaultMessage;
  }

  /**
   * 获取Code。
   *
   * @return 返回对应结果。
   */
  @Override
  public String getCode() {
    return code;
  }

  /**
   * 获取HttpStatus。
   *
   * @return 返回对应结果。
   */
  @Override
  public HttpStatus getHttpStatus() {
    return httpStatus;
  }

  /**
   * 获取DefaultMessage。
   *
   * @return 返回对应结果。
   */
  @Override
  public String getDefaultMessage() {
    return defaultMessage;
  }

}
