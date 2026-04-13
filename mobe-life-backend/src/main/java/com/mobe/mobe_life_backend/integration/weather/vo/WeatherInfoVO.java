package com.mobe.mobe_life_backend.integration.weather.vo;

import lombok.Data;

@Data
public class WeatherInfoVO {

  /**
   * 城市名称
   */
  private String city;

  /**
   * 天气文案，例如 多云 / 晴
   */
  private String weatherText;

  /**
   * 温度，原始值，例如 21
   */
  private String temperature;

  /**
   * 天气图标编码，可先预留
   */
  private String icon;
}