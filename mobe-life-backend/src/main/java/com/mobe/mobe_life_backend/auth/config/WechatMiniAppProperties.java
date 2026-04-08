package com.mobe.mobe_life_backend.auth.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 微信小程序配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "wechat.miniapp")
public class WechatMiniAppProperties {

  /**
   * 小程序 AppID
   */
  private String appId;

  /**
   * 小程序 AppSecret
   */
  private String appSecret;
}