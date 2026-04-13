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