/**
 * 核心职责：为控制层请求记录统一的入口和出口日志。
 * 所属业务模块：系统基础设施 / 运行时观测。
 * 重要依赖关系或外部约束：依赖 Spring AOP；日志中会打印方法参数和返回值，生产环境需关注敏感信息脱敏。
 */
package com.mobe.mobe_life_backend.config;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.UUID;

/**
 * 控制层日志切面。
 *
 * <p>设计初衷是为每次请求建立统一 traceId，便于把“入口参数”“执行耗时”“返回结果”串联起来，
 * 在没有完整链路追踪系统时也能快速定位问题。</p>
 *
 * <p>线程安全性：无共享可变状态；traceId 为每次调用局部生成。</p>
 */
@Aspect
@Component
@Slf4j
public class LogAspect {

  /**
   * 环绕记录控制层日志。
   *
   * @param joinPoint 切点信息，不允许为 null。
   * @return 原始控制器返回值。
   * @throws Throwable 原始执行过程中抛出的任何异常都会继续向上抛出。
   * @implNote 当前切点覆盖所有 controller 包下的方法。
   */
  @Around("execution(* com.mobe.mobe_life_backend..controller..*.*(..))")
  public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
    long startTime = System.currentTimeMillis();
    String traceId = UUID.randomUUID().toString().replace("-", "");

    ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    HttpServletRequest request = attributes != null ? attributes.getRequest() : null;

    String requestUri = request != null ? request.getRequestURI() : "";
    String method = request != null ? request.getMethod() : "";

    log.info("request start, traceId={}, method={}, uri={}, args={}",
        traceId, method, requestUri, joinPoint.getArgs());

    Object result = joinPoint.proceed();

    long duration = System.currentTimeMillis() - startTime;
    log.info("request end, traceId={}, uri={}, duration={}ms, result={}",
        traceId, requestUri, duration, result);

    return result;
  }
}
