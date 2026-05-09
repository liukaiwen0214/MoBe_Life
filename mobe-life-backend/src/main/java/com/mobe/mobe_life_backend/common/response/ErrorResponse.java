/**
 * 定义标准错误响应结构，保证错误响应格式一致。
 * 模块：公共基础设施 / 响应模型。
 * 约束：用于 GlobalExceptionHandler 统一处理异常时返回。
 */
package com.mobe.mobe_life_backend.common.response;

import com.mobe.mobe_life_backend.common.exception.ErrorDetail;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

/**
 * 标准错误响应。
 *
 * <p>提供一个统一的错误响应格式，包含足够的信息以便前端处理和排查问题。</p>
 */
@Data
@Builder
public class ErrorResponse {

  /**
   * 错误发生的时间戳。
   */
  private Instant timestamp;
  
  /**
   * HTTP 状态码。
   */
  private Integer status;
  
  /**
   * 业务错误码。
   */
  private String code;
  
  /**
   * 错误消息。
   */
  private String message;
  
  /**
   * 请求路径。
   */
  private String path;
  
  /**
   * 请求 ID，用于追踪和排查问题。
   */
  private String requestId;
  
  /**
   * 错误详情，用于参数校验失败等场景。
   */
  private List<ErrorDetail> details;

}