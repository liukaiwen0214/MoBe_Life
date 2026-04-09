/**
 * 核心职责：统一收口控制层抛出的异常，保证接口始终返回可预期的响应结构。
 * 所属业务模块：公共基础设施 / Web 异常处理。
 * 重要依赖关系或外部约束：依赖 Spring MVC 异常分派机制；所有异常最终都会被包装为 `Result`。
 */
package com.mobe.mobe_life_backend.common.exception;

import com.mobe.mobe_life_backend.common.constant.ErrorCode;
import com.mobe.mobe_life_backend.common.response.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * 全局异常处理器。
 *
 * <p>设计初衷是把异常到 HTTP 响应的转换逻辑集中在同一个地方，
 * 避免各个 Controller 复制 try-catch，同时让前端拿到稳定的错误码和消息结构。</p>
 *
 * <p>线程安全性：无可变共享状态，线程安全。</p>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

  /**
   * 处理业务异常。
   *
   * @param e 业务异常，不允许为 null。
   * @return HTTP 400 响应；响应体中的错误码和消息来自异常本身。
   */
  @ExceptionHandler(BusinessException.class)
  public ResponseEntity<Result<?>> handleBusinessException(BusinessException e) {
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(Result.error(e.getCode(), e.getMessage()));
  }

  /**
   * 处理 `@RequestBody` / `@Valid` 触发的参数校验异常。
   *
   * @param e 参数校验异常，不允许为 null。
   * @return HTTP 400 响应；优先返回第一个字段错误，避免一次性把大量校验细节暴露给客户端。
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Result<?>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
    String message = "参数错误";
    if (e.getBindingResult().hasFieldErrors()) {
      message = e.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
    }
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(Result.error(ErrorCode.PARAMS_ERROR, message));
  }

  /**
   * 处理路径参数、请求参数级别的校验异常。
   *
   * @param e 约束违规异常，不允许为 null。
   * @return HTTP 400 响应。
   */
  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<Result<?>> handleConstraintViolationException(ConstraintViolationException e) {
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(Result.error(ErrorCode.PARAMS_ERROR, e.getMessage()));
  }

  /**
   * 处理未命中资源。
   *
   * @param e 资源缺失异常，不允许为 null。
   * @param request 当前请求，不允许为 null。
   * @return HTTP 404 响应。
   * @implNote 对常见浏览器探测资源做了单独兼容，避免这些噪音请求干扰业务接口排查。
   */
  @ExceptionHandler(NoResourceFoundException.class)
  public ResponseEntity<Result<?>> handleNoResourceFoundException(
      NoResourceFoundException e,
      HttpServletRequest request) {

    String path = request.getRequestURI();

    if ("/icon/favicon.ico".equals(path)
        || "/.well-known/appspecific/com.chrome.devtools.json".equals(path)) {
      return ResponseEntity
          .status(HttpStatus.NOT_FOUND)
          .body(Result.error(ErrorCode.NOT_FOUND_ERROR, "资源不存在"));
    }

    return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body(Result.error(ErrorCode.NOT_FOUND_ERROR, "请求资源不存在"));
  }

  /**
   * 处理兜底未分类异常。
   *
   * @param e 任意异常，不允许为 null。
   * @return HTTP 500 响应；对客户端隐藏内部实现细节。
   * @implNote 当前直接输出到标准错误，适合开发阶段；生产环境更建议接入结构化日志。
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<Result<?>> handleException(Exception e) {
    System.err.println("系统异常: " + e.getClass().getName() + " - " + e.getMessage());
    return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(Result.error(ErrorCode.SYSTEM_ERROR, "系统错误"));
  }
}
