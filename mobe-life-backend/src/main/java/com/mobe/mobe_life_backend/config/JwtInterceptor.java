/**
 * 核心职责：在请求进入控制层前完成 JWT 鉴权，并把用户 ID 写入请求线程上下文。
 * 所属业务模块：认证中心 / 安全拦截层。
 * 重要依赖关系或外部约束：依赖 `JwtUtils` 和 `UserContext`；必须与 `WebMvcConfig` 中的排除路径保持一致。
 */
package com.mobe.mobe_life_backend.config;

import com.mobe.mobe_life_backend.common.context.UserContext;
import com.mobe.mobe_life_backend.common.exception.BusinessException;
import com.mobe.mobe_life_backend.common.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

/**
 * JWT 拦截器。
 *
 * <p>
 * 设计初衷是把鉴权从业务逻辑里抽离出来，确保服务层只处理“当前用户是谁之后要做什么”，
 * 而不重复处理 token 提取、合法性校验和线程上下文注入。
 * </p>
 *
 * <p>
 * 线程安全性：本类无可变共享状态，线程安全；真正需要关注的是 `ThreadLocal` 必须在请求结束后清理。
 * </p>
 */
@Component
public class JwtInterceptor implements HandlerInterceptor {

  /** Bearer token 前缀。 */
  private static final String BEARER_PREFIX = "Bearer ";

  /**
   * 请求前鉴权。
   *
   * @param request  当前 HTTP 请求，不允许为 null。
   * @param response 当前 HTTP 响应，不允许为 null。
   * @param handler  目标处理器，不保证类型。
   * @return 鉴权成功返回 `true`。
   * @throws BusinessException 当 token 缺失、无效或已过期时抛出。
   * @implNote 该方法会把用户 ID 写入 `UserContext`，供后续服务层读取。
   */
  @Override
  public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
      @NonNull Object handler) {
    String token = request.getHeader("Authorization");

    if (token == null || token.isBlank()) {
      throw new BusinessException("未登录或token为空");
    }

    if (token.startsWith(BEARER_PREFIX)) {
      token = token.substring(BEARER_PREFIX.length());
    }

    if (!JwtUtils.isValid(token)) {
      throw new BusinessException("token无效或已过期");
    }

    Long userId = JwtUtils.getUserId(token);
    UserContext.setCurrentUserId(userId);
    return true;
  }

  /**
   * 请求完成后清理线程上下文。
   *
   * @param request  当前请求。
   * @param response 当前响应。
   * @param handler  目标处理器。
   * @param ex       执行过程中产生的异常，允许为 null。
   * @implNote 线程池会复用工作线程，若这里不清理，后续请求可能错误继承上一个用户身份。
   */
  @Override
  public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
      @NonNull Object handler, @Nullable Exception ex) {
    UserContext.clear();
  }
}
