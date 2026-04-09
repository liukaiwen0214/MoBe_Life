/**
 * 核心职责：承接已设置密码账号的改密请求。
 * 所属业务模块：认证中心 / 账号安全。
 * 重要依赖关系或外部约束：只做非空校验，密码一致性、长度和旧密码正确性由服务层结合用户状态判断。
 */
package com.mobe.mobe_life_backend.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 修改密码请求体。
 *
 * <p>设计初衷是显式区分“设置初始密码”和“基于旧密码改密”两个业务动作，
 * 防止未做旧密码校验的逻辑误用于已设置密码用户。</p>
 *
 * <p>线程安全性：DTO 仅用于当前请求生命周期，不能在日志或缓存中长期保留明文密码。</p>
 */
@Data
public class ChangePasswordDTO {

  /**
   * 用户当前密码明文。
   * 不允许为空；服务层会用 BCrypt 与数据库密文比对，绝不能直接持久化。
   */
  @NotBlank(message = "原密码不能为空")
  private String oldPassword;

  /**
   * 目标新密码明文。
   * 不允许为空；当前业务规则要求长度至少 6 位。
   */
  @NotBlank(message = "新密码不能为空")
  private String newPassword;

  /**
   * 新密码确认值。
   * 用于在后端再次确认客户端没有因为输入错误造成不可逆改密。
   */
  @NotBlank(message = "确认密码不能为空")
  private String confirmPassword;
}
