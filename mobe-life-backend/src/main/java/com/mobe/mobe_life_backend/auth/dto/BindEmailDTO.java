/**
 * 核心职责：承接“当前登录用户绑定邮箱”请求参数。
 * 所属业务模块：认证中心 / 账号安全。
 * 重要依赖关系或外部约束：字段校验依赖 Jakarta Validation；邮箱与验证码会在服务层进一步结合业务状态校验。
 */
package com.mobe.mobe_life_backend.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 绑定邮箱请求体。
 *
 * <p>设计初衷是把“邮箱地址”和“用户提交的验证码”作为一个原子命令传入服务层，
 * 保证绑定动作在业务上围绕同一目标邮箱执行，而不是分别传递两个松散参数。</p>
 *
 * <p>线程安全性：DTO 仅在单次请求线程中短暂使用，不应在异步流程中共享。</p>
 */
@Data
public class BindEmailDTO {

  /**
   * 待绑定邮箱。
   * 只做格式级校验，服务层仍会统一转小写并检查是否已被其他账号占用。
   */
  @NotBlank(message = "邮箱不能为空")
  @Email(message = "邮箱格式不正确")
  private String email;

  /**
   * 用户收到的验证码明文。
   * 不能为空，长度在当前实现中默认为 6 位；真正的正确性由服务层按 hash 比对。
   */
  @NotBlank(message = "验证码不能为空")
  private String code;
}
