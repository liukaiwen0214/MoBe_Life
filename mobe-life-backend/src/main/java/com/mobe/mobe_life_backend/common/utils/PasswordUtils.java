/**
 * 核心职责：提供统一的密码加密与比对工具，避免业务层直接操作加密器实例。
 * 所属业务模块：公共基础设施 / 安全工具。
 * 重要依赖关系或外部约束：依赖 Spring Security 的 BCrypt 实现；明文密码只应短暂存在于调用栈中。
 */
package com.mobe.mobe_life_backend.common.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 密码工具类。
 *
 * <p>设计初衷是把密码算法选择集中起来，避免系统里出现多种加密方式并存导致无法统一验证。</p>
 *
 * <p>线程安全性：`BCryptPasswordEncoder` 可安全复用，本类只暴露静态方法，线程安全。</p>
 */
public class PasswordUtils {

  /** 全局复用的 BCrypt 编码器。 */
  private static final BCryptPasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();

  private PasswordUtils() {
  }

  /**
   * 加密密码。
   *
   * @param rawPassword 明文密码，不允许为 null 或空白。
   * @return BCrypt 密文，不返回 null；同一明文多次加密结果不同属于正常行为。
   */
  public static String encode(String rawPassword) {
    return PASSWORD_ENCODER.encode(rawPassword);
  }

  /**
   * 校验密码是否匹配。
   *
   * @param rawPassword 用户输入的明文密码，不允许为 null。
   * @param encodedPassword 数据库存储的 BCrypt 密文，不允许为 null。
   * @return 匹配返回 `true`，否则返回 `false`；不会抛出业务异常。
   */
  public static boolean matches(String rawPassword, String encodedPassword) {
    return PASSWORD_ENCODER.matches(rawPassword, encodedPassword);
  }
}
