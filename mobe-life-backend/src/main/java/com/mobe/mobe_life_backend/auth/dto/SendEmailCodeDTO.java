/**
 * 核心职责：承接发送绑定邮箱验证码请求。
 * 所属业务模块：认证中心 / 验证码体系。
 * 重要依赖关系或外部约束：仅负责邮箱格式校验，频控、占用校验和验证码生命周期由服务层保证。
 */
package com.mobe.mobe_life_backend.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 发送邮箱验证码请求体。
 *
 * <p>设计初衷是把“验证码发送”从“邮箱绑定确认”中拆开，便于服务层记录发送日志、做频率限制，
 * 也便于未来扩展不同业务场景的邮件模板。</p>
 */
@Data
public class SendEmailCodeDTO {

  /**
   * 目标邮箱地址。
   * 不能为空且必须满足邮箱格式；服务层会统一规范化为小写，避免大小写差异导致重复绑定。
   */
  @NotBlank(message = "邮箱不能为空")
  @Email(message = "邮箱格式不正确")
  private String email;
}
