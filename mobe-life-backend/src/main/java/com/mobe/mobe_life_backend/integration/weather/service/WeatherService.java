/**
 * 核心职责：承载外部集成相关代码，是该模块实现中的一个组成单元。
 * 所属业务模块：外部集成 / weather。
 * 重要依赖关系或外部约束：需要与同模块中的控制层、服务层和数据结构保持语义一致。
 */
package com.mobe.mobe_life_backend.integration.weather.service;

import com.mobe.mobe_life_backend.integration.weather.vo.WeatherInfoVO;

public interface WeatherService {

  /**
   * 根据经纬度获取天气信息
   *
   * @param latitude  纬度
   * @param longitude 经度
   * @return 天气信息
   */
  WeatherInfoVO getWeatherByLocation(Double latitude, Double longitude);
}