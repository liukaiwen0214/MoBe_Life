package com.mobe.mobe_life_backend.config;

import com.mobe.mobe_life_backend.common.context.UserContext;
import com.mobe.mobe_life_backend.common.exception.BusinessException;
import com.mobe.mobe_life_backend.common.utils.JwtUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtInterceptor implements HandlerInterceptor {

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
    String token = request.getHeader("Authorization");

    if (token == null || token.isBlank()) {
      throw new BusinessException("未登录或token为空");
    }

    if (token.startsWith("Bearer ")) {
      token = token.substring(7);
    }

    if (!JwtUtils.isValid(token)) {
      throw new BusinessException("token无效或已过期");
    }

    Long userId = JwtUtils.getUserId(token);
    UserContext.setCurrentUserId(userId);

    return true;
  }

  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
    UserContext.clear();
  }
}