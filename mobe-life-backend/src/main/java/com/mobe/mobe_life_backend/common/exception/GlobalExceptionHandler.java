/**
 * 核心职责：统一收口控制层抛出的异常，保证接口始终返回可预期的响应结构。
 * 所属业务模块：公共基础设施 / Web 异常处理。
 * 重要依赖关系或外部约束：依赖 Spring MVC 异常分派机制；所有异常最终都会被包装为 `Result` 或 `ErrorResponse`。
 */
package com.mobe.mobe_life_backend.common.exception;

import com.mobe.mobe_life_backend.common.response.ErrorResponse;
import com.mobe.mobe_life_backend.common.response.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 全局异常处理器。
 *
 * <p>
 * 设计初衷是把异常到 HTTP 响应的转换逻辑集中在同一个地方，
 * 避免各个 Controller 复制 try-catch，同时让前端拿到稳定的错误码和消息结构。
 * </p>
 *
 * <p>
 * 线程安全性：无可变共享状态，线程安全。
 * </p>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

  /**
   * 处理业务异常。
   *
   * @param ex      业务异常，不允许为 null。
   * @param request 当前请求，不允许为 null。
   * @return HTTP 响应；响应体为标准 ErrorResponse。
   */
  @ExceptionHandler(BusinessException.class)
  public ResponseEntity<?> handleBusinessException(
      BusinessException ex,
      HttpServletRequest request) {
    // 检查是否使用新的错误码体系
    if (ex.getErrorCode() != null) {
      BaseErrorCode errorCode = ex.getErrorCode();
      String requestId = getRequestId();

      String message = ex.getCustomMessage();
      if (message == null || message.isBlank()) {
        message = errorCode.getDefaultMessage();
      }

      ErrorResponse response = ErrorResponse.builder()
          .timestamp(Instant.now())
          .status(errorCode.getHttpStatus().value())
          .code(errorCode.getCode())
          .message(message)
          .path(request.getRequestURI())
          .requestId(requestId)
          .details(Collections.emptyList())
          .build();

      return new ResponseEntity<>(response, errorCode.getHttpStatus());
    } else {
      // 兼容旧的错误码体系
      return ResponseEntity
          .status(HttpStatus.BAD_REQUEST)
          .body(Result.error(ex.getCode(), ex.getMessage()));
    }
  }

  /**
   * 处理 `@RequestBody` / `@Valid` 触发的参数校验异常。
   *
   * @param ex      参数校验异常，不允许为 null。
   * @param request 当前请求，不允许为 null。
   * @return HTTP 400 响应；响应体为标准 ErrorResponse，包含字段错误详情。
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException ex,
      HttpServletRequest request) {
    List<ErrorDetail> details = new ArrayList<>();
    ex.getBindingResult().getFieldErrors().forEach(fieldError -> {
      ErrorDetail detail = ErrorDetail.builder()
          .field(fieldError.getField())
          .reason(fieldError.getDefaultMessage())
          .build();
      details.add(detail);
    });

    String message = "参数校验失败";
    if (!details.isEmpty()) {
      message = details.get(0).getReason();
    }

    ErrorResponse response = ErrorResponse.builder()
        .timestamp(Instant.now())
        .status(HttpStatus.BAD_REQUEST.value())
        .code(CommonErrorCode.PARAMS_VALIDATION_FAILED.getCode())
        .message(message)
        .path(request.getRequestURI())
        .requestId(getRequestId())
        .details(details)
        .build();

    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  /**
   * 处理路径参数、请求参数级别的校验异常。
   *
   * @param ex      约束违规异常，不允许为 null。
   * @param request 当前请求，不允许为 null。
   * @return HTTP 400 响应；响应体为标准 ErrorResponse。
   */
  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ErrorResponse> handleConstraintViolationException(
      ConstraintViolationException ex,
      HttpServletRequest request) {
    ErrorResponse response = ErrorResponse.builder()
        .timestamp(Instant.now())
        .status(HttpStatus.BAD_REQUEST.value())
        .code(CommonErrorCode.PARAMS_VALIDATION_FAILED.getCode())
        .message(ex.getMessage())
        .path(request.getRequestURI())
        .requestId(getRequestId())
        .details(Collections.emptyList())
        .build();

    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  /**
   * 处理未命中资源。
   *
   * @param ex      资源缺失异常，不允许为 null。
   * @param request 当前请求，不允许为 null。
   * @return HTTP 404 响应；响应体为标准 ErrorResponse。
   * @implNote 对常见浏览器探测资源做了单独兼容，避免这些噪音请求干扰业务接口排查。
   */
  @ExceptionHandler(NoResourceFoundException.class)
  public ResponseEntity<ErrorResponse> handleNoResourceFoundException(
      NoResourceFoundException ex,
      HttpServletRequest request) {
    String path = request.getRequestURI();
    String message = "请求资源不存在";

    if ("/icon/favicon.ico".equals(path)
        || "/.well-known/appspecific/com.chrome.devtools.json".equals(path)) {
      message = "资源不存在";
    }

    ErrorResponse response = ErrorResponse.builder()
        .timestamp(Instant.now())
        .status(HttpStatus.NOT_FOUND.value())
        .code(CommonErrorCode.RESOURCE_NOT_FOUND.getCode())
        .message(message)
        .path(path)
        .requestId(getRequestId())
        .details(Collections.emptyList())
        .build();

    return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
  }

  /**
   * 处理兜底未分类异常。
   *
   * @param ex      任意异常，不允许为 null。
   * @param request 当前请求，不允许为 null。
   * @return HTTP 500 响应；响应体为标准 ErrorResponse，对客户端隐藏内部实现细节。
   * @implNote 当前直接输出到标准错误，适合开发阶段；生产环境更建议接入结构化日志。
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleException(
      Exception ex,
      HttpServletRequest request) {
    System.err.println("系统异常: " + ex.getClass().getName() + " - " + ex.getMessage());
    ex.printStackTrace();

    ErrorResponse response = ErrorResponse.builder()
        .timestamp(Instant.now())
        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
        .code(CommonErrorCode.INTERNAL_SERVER_ERROR.getCode())
        .message("系统错误")
        .path(request.getRequestURI())
        .requestId(getRequestId())
        .details(Collections.emptyList())
        .build();

    return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  /**
   * 从 MDC 中获取 requestId。
   * 优先从 traceId 获取，然后从 requestId 获取。
   *
   * @return requestId 或 null
   */
  private String getRequestId() {
    String requestId = MDC.get("traceId");
    if (requestId == null || requestId.isBlank()) {
      requestId = MDC.get("requestId");
    }
    return requestId;
  }
}
