package com.mobe.mobe_life_backend.common.context;

public class UserContext {

  private static final ThreadLocal<Long> USER_ID_HOLDER = new ThreadLocal<>();

  private UserContext() {
  }

  /**
   * 设置当前登录用户 id
   */
  public static void setCurrentUserId(Long userId) {
    USER_ID_HOLDER.set(userId);
  }

  /**
   * 获取当前登录用户 id
   */
  public static Long getCurrentUserId() {
    return USER_ID_HOLDER.get();
  }

  /**
   * 清理当前线程中的用户信息
   */
  public static void clear() {
    USER_ID_HOLDER.remove();
  }
}