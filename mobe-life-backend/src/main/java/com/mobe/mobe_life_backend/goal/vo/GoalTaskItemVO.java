package com.mobe.mobe_life_backend.goal.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@Schema(description = "目标详情-待办项")
public class GoalTaskItemVO {

  @Schema(description = "待办ID")
  private Long id;

  @Schema(description = "待办标题")
  private String title;

  @Schema(description = "状态编码")
  private String statusCode;

  @Schema(description = "状态名称")
  private String statusText;

  @Schema(description = "截止时间")
  private LocalDateTime deadlineTime;
}