package com.mobe.mobe_life_backend.tool.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "每日一句数据")
public class DailyQuoteVO {

  @Schema(description = "文案内容", example = "大多数人的人生就是这样，你追求的梦想，不一定会在终点给你惊喜，但至少，它会支撑你出发。")
  private String text;

  @Schema(description = "出处", example = "张寒寺《我们这个世界的羊》")
  private String from;

  @Schema(description = "配图地址", example = "http://image.wufazhuce.com/xxx")
  private String imageUrl;
}