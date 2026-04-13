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