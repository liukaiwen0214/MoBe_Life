/**
 * 核心职责：验证公共安全工具类的基础行为是否符合预期。
 * 所属业务模块：后端测试 / 公共工具回归验证。
 * 重要依赖关系或外部约束：这些测试主要用于开发期人工观察输出，不是严格断言型单元测试。
 */
package com.mobe.mobe_life_backend;

import com.mobe.mobe_life_backend.common.utils.JwtUtils;
import com.mobe.mobe_life_backend.common.utils.PasswordUtils;
import org.junit.jupiter.api.Test;

/**
 * 公共工具测试。
 *
 * <p>设计初衷是快速验证密码加密和 JWT 生成解析链路在当前依赖版本下可正常工作，
 * 避免底层库升级后出现明显兼容性问题却无人察觉。</p>
 */
public class CommonUtilsTest {

  /**
   * 验证密码加密与匹配流程。
   *
   * @implNote 当前测试通过控制台输出人工确认；若后续需要更稳定的 CI 保障，建议补充断言。
   */
  @Test
  void testPasswordUtils() {
    String rawPassword = "123456";
    String encodedPassword = PasswordUtils.encode(rawPassword);

    System.out.println("加密后密码: " + encodedPassword);
    System.out.println("密码是否匹配: " + PasswordUtils.matches(rawPassword, encodedPassword));
  }

  /**
   * 验证 JWT 的生成、解析与校验流程。
   *
   * @implNote 该测试主要用来确认工具类与当前密钥配置、JJWT 版本兼容。
   */
  @Test
  void testJwtUtils() {
    String token = JwtUtils.createToken(1L);

    System.out.println("token: " + token);
    System.out.println("userId: " + JwtUtils.getUserId(token));
    System.out.println("isValid: " + JwtUtils.isValid(token));
  }
}
