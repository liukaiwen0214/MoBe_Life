package com.mobe.mobe_life_backend.goal.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
@Schema(description = "目标详情")
public class GoalDetailVO {

  @Schema(description = "目标ID")
  private Long id;

  @Schema(description = "目标标题")
  private String title;

  @Schema(description = "目标内容")
  private String content;

  @Schema(description = "备注")
  private String remark;

  @Schema(description = "执行模式")
  private String executionMode;

  @Schema(description = "状态模板ID")
  private Long statusTemplateId;

  @Schema(description = "当前状态ID")
  private Long currentStatusId;

  // @Schema(description = "当前状态编码")
  // private String statusCode;

  // @Schema(description = "当前状态名称")
  // private String statusText;

  @Schema(description = "节点数量")
  private Integer nodeCount;

  @Schema(description = "待办数量")
  private Integer taskCount;

  @Schema(description = "已完成待办数量")
  private Integer completedCount;

  @Schema(description = "总待办数量")
  private Integer totalCount;

  @Schema(description = "更新时间")
  private LocalDateTime updateTime;

  @Schema(description = "节点列表")
  private List<GoalNodeItemVO> nodes;

  @Schema(description = "待办列表")
  private List<GoalTaskItemVO> tasks;

  @Schema(description = "状态列表")
  private List<GoalStatusItemVO> statusList;

  @Schema(description = "执行日志")
  private List<GoalLogItemVO> logs;
  @Schema(description = "是否已完成：0-未完成 1-已完成")
  private Integer isCompleted;

  @Schema(description = "完成时间")
  private LocalDateTime completedTime;
}