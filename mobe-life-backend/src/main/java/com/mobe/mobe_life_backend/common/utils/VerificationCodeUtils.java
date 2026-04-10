/**
 * 核心职责：提供验证码生成与摘要计算能力。
 * 所属业务模块：公共基础设施 / 验证码工具。
 * 重要依赖关系或外部约束：当前实现默认生成 6 位数字验证码，摘要算法依赖固定盐值。
 */
package com.mobe.mobe_life_backend.common.utils;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.digest.DigestUtil;

/**
 * 验证码工具类。
 *
 * <p>
 * 设计初衷是统一验证码格式和摘要策略，避免不同业务场景各自实现导致校验口径不一致。
 * </p>
 *
 * <p>
 * 线程安全性：纯静态无状态工具类，线程安全。
 * </p>
 */
public class VerificationCodeUtils {

  /**
   * 验证码摘要盐值。
   * 业务上用来防止相同目标与相同验证码在数据库中呈现可直接枚举的固定摘要模式。
   */
  private static final String CODE_SALT = "mobe_email_code_salt";

  private VerificationCodeUtils() {
  }

  /**
   * 生成 6 位数字验证码。
   *
   * @return 6 位数字字符串，不返回 null。
   * @implNote 纯数字更适合用户在手机端输入，但安全强度依赖有效期、频控和失败次数控制共同保障。
   */
  public static String generateCode() {
    return RandomUtil.randomNumbers(6);
  }

  /**
   * * 生成验证码 Key。
   * 
   * @return 验证码 Key，不返回 null。
   * @implNote 验证码 Key 用于关联验证码图片和验证码摘要，必须保证唯一性和不可预测性。
   */
  public static String generateCaptchaKey() {
    return IdUtil.fastSimpleUUID();
  }

  /**
   * 计算验证码摘要。
   *
   * @param target  验证目标，不允许为 null；如邮箱地址。
   * @param bizType 业务场景，不允许为 null；如 `BIND_EMAIL`。
   * @param code    验证明文，不允许为 null。
   * @return SHA-256 摘要，不返回 null。
   * @implNote 把目标和业务场景一起纳入摘要，是为了防止同一验证码跨场景复用。
   */
  public static String hashCode(String target, String bizType, String code) {
    return DigestUtil.sha256Hex(target + ":" + bizType + ":" + code + ":" + CODE_SALT);
  }
}
