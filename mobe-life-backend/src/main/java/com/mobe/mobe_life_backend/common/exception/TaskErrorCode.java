/**
 * 核心职责：定义任务模块的错误码。
 * 所属业务模块：任务模块 / 错误码体系。
 * 重要依赖关系或外部约束：实现 BaseErrorCode 接口，确保错误码格式和属性的一致性。
 */
package com.mobe.mobe_life_backend.common.exception;

import org.springframework.http.HttpStatus;

/**
 * 任务模块错误码枚举。
 *
 * <p>包含与任务相关的错误码，如任务创建、修改、状态变更等错误。</p>
 */
public enum TaskErrorCode implements BaseErrorCode {

  /** 任务标题不能为空 */
  TASK_TITLE_EMPTY("TSK-1001", HttpStatus.BAD_REQUEST, "任务标题不能为空"),
  
  /** 截止时间不能早于开始时间 */
  DEADLINE_BEFORE_START("TSK-1002", HttpStatus.BAD_REQUEST, "截止时间不能早于开始时间"),
  
  /** 重复规则配置无效 */
  INVALID_REPEAT_RULE("TSK-1003", HttpStatus.BAD_REQUEST, "重复规则配置无效"),
  
  /** 已完成任务不可修改 */
  COMPLETED_TASK_CANNOT_MODIFY("TSK-2001", HttpStatus.BAD_REQUEST, "已完成任务不可修改"),
  
  /** 不允许的任务状态流转 */
  INVALID_STATUS_TRANSITION("TSK-2002", HttpStatus.BAD_REQUEST, "不允许的任务状态流转"),
  
  /** 待办归属对象不存在 */
  TODO_OWNER_NOT_FOUND("TSK-2003", HttpStatus.NOT_FOUND, "待办归属对象不存在"),
  
  /** 当前状态模板下无该状态 */
  STATUS_NOT_IN_TEMPLATE("TSK-2004", HttpStatus.BAD_REQUEST, "当前状态模板下无该状态"),
  
  /** 独立待办不允许绑定节点 */
  STANDALONE_TODO_CANNOT_BIND_NODE("TSK-2005", HttpStatus.BAD_REQUEST, "独立待办不允许绑定节点"),
  
  /** 无权限操作该任务 */
  NO_PERMISSION_OPERATE_TASK("TSK-3001", HttpStatus.FORBIDDEN, "无权限操作该任务"),
  
  /** 任务不存在 */
  TASK_NOT_FOUND("TSK-4001", HttpStatus.NOT_FOUND, "任务不存在"),
  
  /** 提醒不存在 */
  REMINDER_NOT_FOUND("TSK-4002", HttpStatus.NOT_FOUND, "提醒不存在"),
  
  /** 重复任务配置不存在 */
  REPEAT_TASK_CONFIG_NOT_FOUND("TSK-4003", HttpStatus.NOT_FOUND, "重复任务配置不存在");

  private final String code;
  private final HttpStatus httpStatus;
  private final String defaultMessage;

  TaskErrorCode(String code, HttpStatus httpStatus, String defaultMessage) {
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
