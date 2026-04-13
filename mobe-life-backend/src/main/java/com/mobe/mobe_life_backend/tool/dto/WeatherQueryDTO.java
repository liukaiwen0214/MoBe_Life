package com.mobe.mobe_life_backend.tool.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "天气查询参数")
public class WeatherQueryDTO {

  @NotNull(message = "纬度不能为空")
  @DecimalMin(value = "-90.0", message = "纬度不合法")
  @DecimalMax(value = "90.0", message = "纬度不合法")
  @Schema(description = "纬度", requiredMode = Schema.RequiredMode.REQUIRED, example = "36.651200")
  private Double latitude;

  @NotNull(message = "经度不能为空")
  @DecimalMin(value = "-180.0", message = "经度不合法")
  @DecimalMax(value = "180.0", message = "经度不合法")
  @Schema(description = "经度", requiredMode = Schema.RequiredMode.REQUIRED, example = "117.120100")
  private Double longitude;
}