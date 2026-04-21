package com.mobe.mobe_life_backend.task.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
@Schema(description = "待办详情")
public class TaskDetailVO {

  @Schema(description = "待办ID")
  private Long id;

  @Schema(description = "待办标题")
  private String title;

  @Schema(description = "待办内容")
  private String content;

  @Schema(description = "备注")
  private String remark;

  @Schema(description = "归属类型")
  private String directOwnerType;

  @Schema(description = "归属对象ID")
  private Long directOwnerId;

  @Schema(description = "归属对象名称")
  private String ownerName;

  @Schema(description = "状态模板ID")
  private Long statusTemplateId;

  @Schema(description = "当前状态ID")
  private Long currentStatusId;

  @Schema(description = "状态编码")
  private String statusCode;

  @Schema(description = "状态名称")
  private String statusText;

  @Schema(description = "计划开始时间")
  private LocalDateTime planStartTime;

  @Schema(description = "计划结束时间")
  private LocalDateTime planEndTime;

  @Schema(description = "截止时间")
  private LocalDateTime deadlineTime;

  @Schema(description = "实际开始时间")
  private LocalDateTime actualStartTime;

  @Schema(description = "完成时间")
  private LocalDateTime completedAt;

  @Schema(description = "更新时间")
  private LocalDateTime updateTime;

  @Schema(description = "状态列表")
  private List<TaskStatusItemVO> statusList;

  @Schema(description = "操作日志")
  private List<TaskLogItemVO> logs;
  @Schema(description = "状态变更时间线")
  private List<TaskStatusChangeLogItemVO> statusChangeLogs;
}