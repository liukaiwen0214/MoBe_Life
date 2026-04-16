/**
 * 核心职责：定义错误码统一接口，为各模块错误码枚举提供标准规范。
 * 所属业务模块：公共基础设施 / 错误码体系。
 * 重要依赖关系或外部约束：所有错误码枚举都应实现此接口，确保错误码格式和属性的一致性。
 */
package com.mobe.mobe_life_backend.common.exception;

import org.springframework.http.HttpStatus;

/**
 * 错误码统一接口。
 *
 * <p>设计初衷是为所有业务错误码提供一个统一的规范，确保错误码包含必要的信息，
 * 如错误码字符串、HTTP 状态码和默认错误消息。</p>
 */
public interface BaseErrorCode {

  /**
   * 获取错误码字符串。
   *
   * @return 错误码字符串，如 "PRJ-4001"
   */
  String getCode();

  /**
   * 获取对应的 HTTP 状态码。
   *
   * @return HTTP 状态码，如 HttpStatus.NOT_FOUND
   */
  HttpStatus getHttpStatus();

  /**
   * 获取默认错误消息。
   *
   * @return 默认错误消息，如 "项目不存在"
   */
  String getDefaultMessage();

}