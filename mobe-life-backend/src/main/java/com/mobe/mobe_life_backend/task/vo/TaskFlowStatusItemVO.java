package com.mobe.mobe_life_backend.task.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "待办流程状态项")
public class TaskFlowStatusItemVO {

  @Schema(description = "状态ID")
  private Long id;

  @Schema(description = "状态编码")
  private String statusCode;

  @Schema(description = "状态名称")
  private String statusName;

  @Schema(description = "排序值")
  private Integer sortNo;

  @Schema(description = "是否初始状态")
  private Integer isInitial;

  @Schema(description = "是否结束状态")
  private Integer isTerminal;

  @Schema(description = "是否启用")
  private Integer isEnabled;

  @Schema(description = "状态颜色")
  private String statusColor;

  @Schema(description = "状态图标")
  private String statusIcon;
}