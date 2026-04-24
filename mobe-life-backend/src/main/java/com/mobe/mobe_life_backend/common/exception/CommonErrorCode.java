/**
 * 核心职责：定义公共/通用模块的错误码。
 * 所属业务模块：公共基础设施 / 错误码体系。
 * 重要依赖关系或外部约束：实现 BaseErrorCode 接口，确保错误码格式和属性的一致性。
 */
package com.mobe.mobe_life_backend.common.exception;

import org.springframework.http.HttpStatus;

/**
 * 公共/通用模块错误码枚举。
 *
 * <p>包含适用于多个模块的通用错误码，如参数错误、系统错误等。</p>
 */
public enum CommonErrorCode implements BaseErrorCode {

  /** 参数校验失败 */
  PARAMS_VALIDATION_FAILED("COM-1001", HttpStatus.BAD_REQUEST, "参数校验失败"),
  
  /** 缺少必填参数 */
  MISSING_REQUIRED_PARAM("COM-1002", HttpStatus.BAD_REQUEST, "缺少必填参数"),
  
  /** 参数格式错误 */
  PARAM_FORMAT_ERROR("COM-1003", HttpStatus.BAD_REQUEST, "参数格式错误"),
  
  /** 数据冲突 */
  DATA_CONFLICT("COM-2001", HttpStatus.CONFLICT, "数据冲突"),
  
  /** 资源不存在 */
  RESOURCE_NOT_FOUND("COM-4001", HttpStatus.NOT_FOUND, "资源不存在"),
  
  /** 服务器内部错误 */
  INTERNAL_SERVER_ERROR("COM-5001", HttpStatus.INTERNAL_SERVER_ERROR, "服务器内部错误"),
  
  /** 服务不可用 */
  SERVICE_UNAVAILABLE("COM-5002", HttpStatus.SERVICE_UNAVAILABLE, "服务不可用"),
  
  /** 数据库操作失败 */
  DATABASE_OPERATION_FAILED("COM-5003", HttpStatus.INTERNAL_SERVER_ERROR, "数据库操作失败");

  private final String code;
  private final HttpStatus httpStatus;
  private final String defaultMessage;

  CommonErrorCode(String code, HttpStatus httpStatus, String defaultMessage) {
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
