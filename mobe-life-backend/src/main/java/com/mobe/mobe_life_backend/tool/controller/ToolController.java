package com.mobe.mobe_life_backend.tool.controller;

import com.mobe.mobe_life_backend.common.response.Result;
import com.mobe.mobe_life_backend.integration.weather.service.WeatherService;
import com.mobe.mobe_life_backend.integration.weather.vo.WeatherInfoVO;
import com.mobe.mobe_life_backend.tool.dto.WeatherQueryDTO;
import com.mobe.mobe_life_backend.tool.vo.WeatherCardVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "工具接口", description = "给小程序前端使用的轻量工具接口")
public class ToolController {

  private final WeatherService weatherService;

  @GetMapping("/api/tool/weather")
  @Operation(summary = "根据经纬度获取天气", description = "用于首页天气展示")
  public Result<WeatherCardVO> getWeather(@Valid WeatherQueryDTO queryDTO) {
    WeatherInfoVO weatherInfo = weatherService.getWeatherByLocation(
        queryDTO.getLatitude(),
        queryDTO.getLongitude());

    WeatherCardVO vo = new WeatherCardVO();
    vo.setCity(weatherInfo.getCity());
    vo.setWeatherText(weatherInfo.getWeatherText());
    vo.setTemperature(formatTemperature(weatherInfo.getTemperature()));
    vo.setExtraText(buildExtraText(weatherInfo.getWeatherText()));

    return Result.success(vo);
  }

  private String formatTemperature(String temperature) {
    if (temperature == null || temperature.isBlank()) {
      return "--°";
    }
    return temperature + "°";
  }

  private String buildExtraText(String weatherText) {
    if (weatherText == null || weatherText.isBlank()) {
      return "愿你今天也有一点轻松的呼吸感";
    }

    if (weatherText.contains("晴")) {
      return "天气不错，适合把想做的事慢慢落下来";
    }
    if (weatherText.contains("雨")) {
      return "外面有点潮，出门记得带伞";
    }
    if (weatherText.contains("云") || weatherText.contains("阴")) {
      return "今天的节奏可以放柔一点";
    }
    if (weatherText.contains("雪")) {
      return "天气偏冷，记得照顾好自己";
    }
    if (weatherText.contains("风")) {
      return "风有点明显，出门多留意一下";
    }
    return "愿你今天也有一点轻松的呼吸感";
  }
}