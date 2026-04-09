/**
 * 核心职责：在单次请求线程内保存当前登录用户 ID，避免业务层反复解析 token。
 * 所属业务模块：公共基础设施 / 请求上下文。
 * 重要依赖关系或外部约束：依赖 `ThreadLocal`；必须在请求结束时清理，否则线程池复用会造成用户串号。
 */
package com.mobe.mobe_life_backend.common.context;

/**
 * 当前用户上下文工具类。
 *
 * <p>设计初衷是让拦截器在鉴权成功后把用户 ID 注入线程上下文，后续 Service 可以直接获取，
 * 避免每个业务方法都显式传递用户 ID。</p>
 *
 * <p>线程安全性：基于 `ThreadLocal` 实现，对单线程请求安全；不适用于异步线程、消息消费线程或线程切换场景，
 * 调用方若启动异步任务必须显式传递用户身份。</p>
 */
public class UserContext {

  /** 当前线程绑定的用户 ID。 */
  private static final ThreadLocal<Long> USER_ID_HOLDER = new ThreadLocal<>();

  private UserContext() {
  }

  /**
   * 设置当前登录用户 ID。
   *
   * @param userId 用户主键，允许为 null；通常由鉴权拦截器在 token 校验通过后设置。
   */
  public static void setCurrentUserId(Long userId) {
    USER_ID_HOLDER.set(userId);
  }

  /**
   * 获取当前登录用户 ID。
   *
   * @return 当前线程中的用户 ID；若未登录或上下文尚未建立则返回 null。
   */
  public static Long getCurrentUserId() {
    return USER_ID_HOLDER.get();
  }

  /**
   * 清理当前线程中的用户信息。
   *
   * @implNote 必须在请求结束后调用，防止容器线程复用导致上一个请求的身份泄漏到下一个请求。
   */
  public static void clear() {
    USER_ID_HOLDER.remove();
  }
}
