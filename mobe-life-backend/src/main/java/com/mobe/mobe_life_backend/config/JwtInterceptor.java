/**
 * 文件级注释：
 * 核心职责：拦截所有 HTTP 请求，验证 JWT Token 有效性，将认证通过的用户 ID 注入当前线程上下文。
 * 所属业务模块：认证授权中心 (Auth Center) - 安全拦截层。
 * 重要依赖：
 * - JwtUtils：Token 解析和验证
 * - UserContext：线程级用户上下文存储
 * - Spring HandlerInterceptor：Spring MVC 拦截器机制
 * 配置约束：
 * - 需在 WebMvcConfig 中注册拦截路径（排除登录、刷新 Token 等公开接口）
 * - 拦截器执行顺序：在 Controller 之前，@ControllerAdvice 之后
 */
package com.mobe.mobe_life_backend.config;

import com.mobe.mobe_life_backend.common.context.UserContext;
import com.mobe.mobe_life_backend.common.exception.BusinessException;
import com.mobe.mobe_life_backend.common.utils.JwtUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * JWT 认证拦截器，实现基于 Token 的无状态身份验证。
 *
 * <p>设计初衷：采用拦截器模式（Interceptor Pattern）集中处理认证逻辑，
 * 避免在每个 Controller 方法中重复编写 Token 校验代码，实现横切关注点分离。</p>
 *
 * <p>在架构中的角色：安全层（Security Layer），作为请求进入业务逻辑前的门卫，
 * 确保只有通过身份验证的请求才能访问受保护资源。</p>
 *
 * <p>认证流程：
 * <pre>
 *   1. 从 HTTP Header 提取 Authorization 字段
 *   2. 去除 "Bearer " 前缀获取纯 Token
 *   3. 使用 JwtUtils 验证 Token 签名和过期时间
 *   4. 提取用户 ID 存入 UserContext（ThreadLocal）
 *   5. 请求处理完成后清理 ThreadLocal（防止内存泄漏）
 * </pre></p>
 *
 * <p>线程安全性：
 * - 本类为 Spring 单例，无实例变量，线程安全
 * - UserContext 使用 ThreadLocal 存储用户 ID，确保线程隔离
 * - 必须在 afterCompletion 中清理 ThreadLocal，防止线程池复用导致的数据污染</p>
 *
 * <p>配置示例（WebMvcConfig）：
 * <pre>
 *   registry.addInterceptor(jwtInterceptor)
 *           .addPathPatterns("/api/**")           // 拦截所有 API
 *           .excludePathPatterns("/api/auth/**"); // 排除认证相关接口
 * </pre></p>
 */
@Component
public class JwtInterceptor implements HandlerInterceptor {

  /**
   * Bearer Token 前缀，符合 OAuth2.0 规范（RFC 6750）。
   *
   * <p>规范说明：
   * - Authorization Header 标准格式：Bearer {access_token}
   * - "Bearer " 共 7 个字符（含空格）
   * - 若客户端未按规范传递，需兼容处理（如直接视为纯 Token）</p>
   */
  private static final String BEARER_PREFIX = "Bearer ";

  /**
   * 前置处理：在 Controller 方法执行前进行身份验证。
   *
   * <p>方法作用：解析并验证 JWT Token，将用户 ID 注入线程上下文。</p>
   *
   * @param request  HTTP 请求对象，用于获取 Header 信息
   * @param response HTTP 响应对象，认证失败时不直接操作（由异常处理器处理）
   * @param handler  目标处理器（Controller 方法），本方法中不使用
   * @return boolean 是否放行请求
   *         - true：认证通过，继续执行 Controller
   *         - false：认证失败（本方法通过抛出异常终止请求，不返回 false）
   *
   * <p>Token 提取逻辑：
   * 1. 从 Header "Authorization" 获取原始值
   * 2. 若为空或空白，抛出 "未登录或token为空"
   * 3. 若以 "Bearer " 开头，截取后 7 位获取纯 Token
   * 4. 若不匹配标准格式，保留原值尝试解析（兼容非标准客户端）</p>
   *
   * <p>异常场景：
   * - Token 为空：BusinessException("未登录或token为空")
   * - Token 无效或过期：BusinessException("token无效或已过期")</p>
   *
   * <p>副作用：
   * 1. 调用 JwtUtils.isValid 验证 Token（CPU 密集型操作）
   * 2. 调用 JwtUtils.getUserId 提取用户 ID
   * 3. 将用户 ID 存入 UserContext.ThreadLocal</p>
   */
  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
    // 从请求头获取 Authorization，可能为 null（未携带）
    String token = request.getHeader("Authorization");

    // 前置校验：Token 必须存在且非空白字符
    if (token == null || token.isBlank()) {
      throw new BusinessException("未登录或token为空");
    }

    // 去除 Bearer 前缀，兼容标准 OAuth2.0 格式
    if (token.startsWith(BEARER_PREFIX)) {
      token = token.substring(BEARER_PREFIX.length());  // 截取 "Bearer " 之后的部分
    }

    // 验证 Token 有效性（签名 + 过期时间）
    if (!JwtUtils.isValid(token)) {
      throw new BusinessException("token无效或已过期");
    }

    // 提取用户 ID 并存入线程上下文，供后续业务逻辑使用
    Long userId = JwtUtils.getUserId(token);
    UserContext.setCurrentUserId(userId);

    // 放行请求，继续执行 Controller
    return true;
  }

  /**
   * 完成处理：在视图渲染完成后清理线程上下文。
   *
   * <p>方法作用：释放 ThreadLocal 中存储的用户 ID，防止内存泄漏和数据污染。</p>
   *
   * @param request  HTTP 请求对象
   * @param response HTTP 响应对象
   * @param handler  目标处理器
   * @param ex       处理过程中抛出的异常（如有），本方法中不使用
   *
   * <p>为什么必须清理：
   * - Tomcat 使用线程池处理请求，线程会被复用
   * - 若不清理，下一个请求可能获取到上一个请求的用户 ID（严重安全漏洞）
   * - ThreadLocal 生命周期与线程绑定，需显式 remove</p>
   *
   * <p>执行时机：
   * - 无论 Controller 是否成功执行，都会调用
   * - 在 @ControllerAdvice 处理异常之后调用
   * - 是请求处理的最后一步</p>
   *
   * <p>副作用：
   * 1. 调用 UserContext.clear() 移除 ThreadLocal 中的用户 ID</p>
   */
  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
    // 清理 ThreadLocal，防止线程池复用导致的数据污染
    UserContext.clear();
  }
}
