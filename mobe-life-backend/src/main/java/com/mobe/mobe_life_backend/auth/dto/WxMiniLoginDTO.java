/**
 * 核心职责：承接微信小程序登录请求中的临时登录凭证。
 * 所属业务模块：认证中心 / 第三方登录。
 * 重要依赖关系或外部约束：`code` 必须由微信 `wx.login` 获取，具备时效性和一次性，后端不能缓存长期复用。
 */
package com.mobe.mobe_life_backend.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 微信小程序登录请求体。
 *
 * <p>设计初衷是把微信登录场景与传统用户名密码登录解耦，
 * 让服务层可以围绕微信官方授权码交换机制建立用户身份。</p>
 */
@Data
public class WxMiniLoginDTO {

  /**
   * 微信小程序登录临时凭证。
   * 不允许为空；通常有效期很短，且被微信服务端消费后不应再次使用。
   */
  @NotBlank(message = "code不能为空")
  private String code;
}
