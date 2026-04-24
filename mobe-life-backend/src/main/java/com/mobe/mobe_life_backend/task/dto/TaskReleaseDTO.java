package com.mobe.mobe_life_backend.task.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "待办放出请求")
public class TaskReleaseDTO {

  @Schema(description = "目标状态ID", requiredMode = Schema.RequiredMode.REQUIRED)
  private Long targetStatusId;

  @Schema(description = "放出说明")
  private String changeRemark;
}