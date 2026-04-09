/**
 * 核心职责：承接微信小程序认证配置，为登录换取 openid 和手机号解密提供统一配置入口。
 * 所属业务模块：认证中心 / 第三方平台接入配置。
 * 重要依赖关系或外部约束：配置项需与微信公众平台创建的小程序保持一致，`appId` 与 `appSecret`
 * 一旦错配，登录和手机号绑定会同时失效。
 */
package com.mobe.mobe_life_backend.auth.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 微信小程序配置载体。
 *
 * <p>设计初衷是把第三方平台凭证从业务实现中抽离出来，确保不同环境可以通过外部配置切换小程序应用，
 * 而不用修改代码。</p>
 *
 * <p>线程安全性：本类由 Spring 以单例方式托管，运行阶段按只读配置使用，线程安全。</p>
 */
@Data
@Component
@ConfigurationProperties(prefix = "wechat.miniapp")
public class WechatMiniAppProperties {

  /**
   * 小程序 AppID。
   * 这是微信识别业务主体的核心标识，必须与前端小程序实际发布应用一致。
   */
  private String appId;

  /**
   * 小程序 AppSecret。
   * 仅允许在服务端保存，用于调用微信换取会话和 access_token 接口。
   */
  private String appSecret;
}
