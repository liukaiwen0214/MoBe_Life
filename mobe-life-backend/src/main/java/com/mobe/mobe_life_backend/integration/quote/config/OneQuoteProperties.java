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