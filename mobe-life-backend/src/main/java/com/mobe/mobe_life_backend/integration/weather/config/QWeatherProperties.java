/**
 * 核心职责：承载外部集成相关代码，是该模块实现中的一个组成单元。
 * 所属业务模块：外部集成 / weather。
 * 重要依赖关系或外部约束：需要与同模块中的控制层、服务层和数据结构保持语义一致。
 */
package com.mobe.mobe_life_backend.integration.weather.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "qweather")
public class QWeatherProperties {

  /**
   * 和风天气接口地址，例如 https://devapi.qweather.com
   */
  private String apiHost;

  /**
   * 和风天气 API Key
   */
  private String apiKey;
}