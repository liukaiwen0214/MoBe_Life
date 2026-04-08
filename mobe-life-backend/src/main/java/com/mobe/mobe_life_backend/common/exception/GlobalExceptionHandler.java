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

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(BusinessException.class)
  public ResponseEntity<Result<?>> handleBusinessException(BusinessException e) {
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(Result.error(e.getCode(), e.getMessage()));
  }

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

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<Result<?>> handleConstraintViolationException(ConstraintViolationException e) {
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(Result.error(ErrorCode.PARAMS_ERROR, e.getMessage()));
  }

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

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Result<?>> handleException(Exception e) {
    System.err.println("系统异常: " + e.getClass().getName() + " - " + e.getMessage());
    return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(Result.error(ErrorCode.SYSTEM_ERROR, "系统错误"));
  }
}