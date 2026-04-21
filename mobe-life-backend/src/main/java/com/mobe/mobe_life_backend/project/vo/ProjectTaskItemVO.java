package com.mobe.mobe_life_backend.project.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@Schema(description = "项目详情-待办项")
public class ProjectTaskItemVO {

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

  @Schema(description = "直属归属类型：PROJECT/NODE")
  private String directOwnerType;

  @Schema(description = "直属归属ID")
  private Long directOwnerId;

  @Schema(description = "所属节点名称")
  private String nodeName;
}