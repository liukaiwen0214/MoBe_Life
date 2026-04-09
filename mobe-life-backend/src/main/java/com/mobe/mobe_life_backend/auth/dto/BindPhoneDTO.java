/**
 * 核心职责：承接微信手机号绑定请求中的临时凭证。
 * 所属业务模块：认证中心 / 账号安全。
 * 重要依赖关系或外部约束：该 `code` 只能由微信小程序 `getPhoneNumber` 场景产生，离开该场景后无法复用。
 */
package com.mobe.mobe_life_backend.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 绑定手机号请求体。
 *
 * <p>设计初衷是让控制层只暴露微信官方推荐的临时凭证，不直接接收明文手机号，
 * 这样可以把手机号真实性校验托管给微信，降低客户端伪造号码的风险。</p>
 *
 * <p>线程安全性：DTO 只在单次请求中使用，不适合缓存或跨线程传递。</p>
 */
@Data
public class BindPhoneDTO {

  /**
   * 微信返回的手机号获取凭证。
   * 不能为空，通常只能使用一次；过期或重复使用会导致微信接口返回业务错误。
   */
  @NotBlank(message = "手机号code不能为空")
  private String code;
}
