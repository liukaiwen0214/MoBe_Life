/**
 * 承载外部集成实现，作为该模块的一个组成单元。
 * 模块：外部集成 / weather。
 * 约束：需要与同模块中的控制层、服务层和数据结构保持语义一致。
 */
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