package com.mobe.mobe_life_backend.task.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "待办列表查询参数")
public class TaskListQueryDTO {

  @Schema(description = "页码")
  private Integer pageNum;

  @Schema(description = "每页数量")
  private Integer pageSize;

  @Schema(description = "关键词")
  private String keyword;

  @Schema(description = "状态编码")
  private String statusCode;

  @Schema(description = "归属类型：PROJECT/GOAL/NODE/INDEPENDENT")
  private String directOwnerType;
}