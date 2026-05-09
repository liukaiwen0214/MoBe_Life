/**
 * 承载外部集成实现，作为该模块的一个组成单元。
 * 模块：外部集成 / quote。
 * 约束：需要与同模块中的控制层、服务层和数据结构保持语义一致。
 */
package com.mobe.mobe_life_backend.integration.quote.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "one")
public class OneQuoteProperties {

  /**
   * ONE 每日一句接口地址
   */
  private String apiUrl;
}