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

@Aspect
@Component
@Slf4j
public class LogAspect {

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