package com.mobe.mobe_life_backend.goal.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "目标详情-节点项")
public class GoalNodeItemVO {

  @Schema(description = "节点ID")
  private Long id;

  @Schema(description = "节点标题")
  private String title;

  @Schema(description = "节点下待办数量")
  private Integer taskCount;
}