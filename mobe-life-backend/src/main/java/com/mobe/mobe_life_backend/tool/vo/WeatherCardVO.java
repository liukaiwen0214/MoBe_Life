package com.mobe.mobe_life_backend.tool.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "首页天气卡片数据")
public class WeatherCardVO {

  @Schema(description = "城市名", example = "济南")
  private String city;

  @Schema(description = "天气文案", example = "多云")
  private String weatherText;

  @Schema(description = "温度", example = "21°")
  private String temperature;

  @Schema(description = "补充文案", example = "今天风有点轻，适合慢慢推进")
  private String extraText;
}