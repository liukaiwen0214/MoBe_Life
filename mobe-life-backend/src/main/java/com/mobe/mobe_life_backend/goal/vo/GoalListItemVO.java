package com.mobe.mobe_life_backend.goal.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@Schema(description = "目标列表项")
public class GoalListItemVO {

  @Schema(description = "目标ID")
  private Long id;

  @Schema(description = "目标标题")
  private String title;

  @Schema(description = "节点数量")
  private Integer nodeCount;

  @Schema(description = "待办数量")
  private Integer taskCount;

  @Schema(description = "已完成待办数量")
  private Integer completedCount;

  @Schema(description = "总待办数量")
  private Integer totalCount;

  @Schema(description = "状态编码")
  private String statusCode;

  @Schema(description = "状态名称")
  private String statusText;

  @Schema(description = "更新时间")
  private LocalDateTime updateTime;
}