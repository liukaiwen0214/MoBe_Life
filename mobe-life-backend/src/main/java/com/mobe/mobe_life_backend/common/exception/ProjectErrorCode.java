/**
 * 核心职责：定义项目模块的错误码。
 * 所属业务模块：项目模块 / 错误码体系。
 * 重要依赖关系或外部约束：实现 BaseErrorCode 接口，确保错误码格式和属性的一致性。
 */
package com.mobe.mobe_life_backend.common.exception;

import org.springframework.http.HttpStatus;

/**
 * 项目模块错误码枚举。
 *
 * <p>包含与项目相关的错误码，如项目创建、修改、删除等错误。</p>
 */
public enum ProjectErrorCode implements BaseErrorCode {

  /** 项目名称不能为空 */
  PROJECT_NAME_EMPTY("PRJ-1001", HttpStatus.BAD_REQUEST, "项目名称不能为空"),
  
  /** 项目下存在任务，无法删除 */
  PROJECT_HAS_TASKS_CANNOT_DELETE("PRJ-2001", HttpStatus.BAD_REQUEST, "项目下存在任务，无法删除"),
  
  /** 项目状态模板不存在 */
  PROJECT_STATUS_TEMPLATE_NOT_FOUND("PRJ-2002", HttpStatus.NOT_FOUND, "项目状态模板不存在"),
  
  /** 项目当前状态不存在 */
  PROJECT_CURRENT_STATUS_NOT_FOUND("PRJ-2003", HttpStatus.BAD_REQUEST, "项目当前状态不存在"),
  
  /** 无权限访问该项目 */
  NO_PERMISSION_ACCESS_PROJECT("PRJ-3001", HttpStatus.FORBIDDEN, "无权限访问该项目"),
  
  /** 项目不存在 */
  PROJECT_NOT_FOUND("PRJ-4001", HttpStatus.NOT_FOUND, "项目不存在");

  private final String code;
  private final HttpStatus httpStatus;
  private final String defaultMessage;

  ProjectErrorCode(String code, HttpStatus httpStatus, String defaultMessage) {
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
