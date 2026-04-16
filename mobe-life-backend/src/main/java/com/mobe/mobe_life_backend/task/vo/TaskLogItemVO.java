package com.mobe.mobe_life_backend.task.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@Schema(description = "待办详情-日志项")
public class TaskLogItemVO {

  @Schema(description = "日志ID")
  private Long id;

  @Schema(description = "日志类型")
  private String logType;

  @Schema(description = "日志描述")
  private String logText;

  @Schema(description = "创建时间")
  private LocalDateTime createTime;
}