/**
 * 核心职责：承载可预期的业务失败，并附带统一错误码。
 * 所属业务模块：公共基础设施 / 异常体系。
 * 重要依赖关系或外部约束：应由 `GlobalExceptionHandler` 统一转换为标准响应，避免异常直接泄漏到客户端。
 */
package com.mobe.mobe_life_backend.common.exception;

import com.mobe.mobe_life_backend.common.constant.ErrorCode;
import lombok.Getter;

/**
 * 业务异常。
 *
 * <p>设计初衷是区分“业务失败”和“系统故障”：前者是用户可感知、可预期的业务拒绝，
 * 应返回可理解的提示；后者才属于需要兜底为系统错误的异常。</p>
 */
@Getter
public class BusinessException extends RuntimeException {

  /**
   * 业务错误码。
   * 不允许为 null；若调用方未指定则默认使用参数错误码，适合大多数校验失败场景。
   */
  private final Integer code;

  /**
   * 使用默认错误码创建业务异常。
   *
   * @param message 错误描述，不允许为 null 或空白；会直接返回给客户端。
   */
  public BusinessException(String message) {
    super(message);
    this.code = ErrorCode.PARAMS_ERROR;
  }

  /**
   * 使用指定错误码创建业务异常。
   *
   * @param code 错误码，不允许为 null。
   * @param message 错误描述，不允许为 null 或空白。
   */
  public BusinessException(Integer code, String message) {
    super(message);
    this.code = code;
  }
}
