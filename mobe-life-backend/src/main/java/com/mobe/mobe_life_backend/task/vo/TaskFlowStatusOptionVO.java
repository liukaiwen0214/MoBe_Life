package com.mobe.mobe_life_backend.task.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "待办流程可选状态")
public class TaskFlowStatusOptionVO {

  @Schema(description = "状态ID")
  private Long id;

  @Schema(description = "状态编码")
  private String statusCode;

  @Schema(description = "状态名称")
  private String statusName;
}