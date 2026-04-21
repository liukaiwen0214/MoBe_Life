package com.mobe.mobe_life_backend.task.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@Schema(description = "待办详情-状态变更日志项")
public class TaskStatusChangeLogItemVO {

  @Schema(description = "日志ID")
  private Long id;

  @Schema(description = "变更类型")
  private String changeType;

  @Schema(description = "变更前状态名称")
  private String fromStatusName;

  @Schema(description = "变更后状态名称")
  private String toStatusName;

  @Schema(description = "变更时间")
  private LocalDateTime changeTime;

  @Schema(description = "变更说明")
  private String changeRemark;
}