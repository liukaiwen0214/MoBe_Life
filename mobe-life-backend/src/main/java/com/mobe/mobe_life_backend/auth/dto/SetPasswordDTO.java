/**
 * 核心职责：承接首次设置密码请求。
 * 所属业务模块：认证中心 / 账号安全。
 * 重要依赖关系或外部约束：该 DTO 对应“账号尚未有密码”的业务路径，不应用在已设置密码后的改密流程。
 */
package com.mobe.mobe_life_backend.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 设置密码请求体。
 *
 * <p>设计初衷是为微信一键登录用户补充本地密码能力，让账号在绑定邮箱或手机号后拥有第二种身份验证手段。</p>
 */
@Data
public class SetPasswordDTO {

  /**
   * 用户希望设置的新密码明文。
   * 不允许为空；服务层会校验长度并完成加密存储。
   */
  @NotBlank(message = "新密码不能为空")
  private String newPassword;

  /**
   * 二次确认密码。
   * 不允许为空；用于避免用户在首次设密时因输入笔误锁死账号。
   */
  @NotBlank(message = "确认密码不能为空")
  private String confirmPassword;
}
