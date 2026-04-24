/**
 * 核心职责：封装工具中心返回给前端或上层调用方的展示模型。
 * 所属业务模块：工具中心 / VO。
 * 重要依赖关系或外部约束：字段设计更偏向展示与接口契约，不一定与数据库结构一一对应。
 */
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