/**
 * 定义错误详情结构，支撑参数校验失败等场景的返回。
 * 模块：公共基础设施 / 错误码体系。
 * 约束：用于 ErrorResponse 中的 details 字段。
 */
package com.mobe.mobe_life_backend.common.exception;

import lombok.Builder;
import lombok.Data;

/**
 * 错误详情。
 *
 * <p>提供详细的错误信息，特别是在参数校验失败时，
 * 可以包含字段名和具体的错误原因。</p>
 */
@Data
@Builder
public class ErrorDetail {

  /**
   * 错误字段名。
   */
  private String field;
  
  /**
   * 错误原因。
   */
  private String reason;

}