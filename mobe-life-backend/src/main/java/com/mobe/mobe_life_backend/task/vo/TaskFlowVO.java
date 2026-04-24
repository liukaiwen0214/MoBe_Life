package com.mobe.mobe_life_backend.task.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Data;

@Data
@Schema(description = "待办流程视图")
public class TaskFlowVO {

  @Schema(description = "待办ID")
  private Long taskId;

  @Schema(description = "当前状态ID")
  private Long currentStatusId;

  @Schema(description = "当前状态编码")
  private String currentStatusCode;

  @Schema(description = "当前状态名称")
  private String currentStatusName;

  @Schema(description = "当前是否终态：0-否 1-是")
  private Integer isTerminal;

  @Schema(description = "是否允许进入下一状态：0-否 1-是")
  private Integer allowNext;

  @Schema(description = "是否允许放出：0-否 1-是")
  private Integer allowRelease;

  @Schema(description = "下一状态")
  private TaskFlowStatusOptionVO nextStatus;

  @Schema(description = "可放出状态列表")
  private List<TaskFlowStatusOptionVO> releaseOptions;

  @Schema(description = "完整流程状态列表")
  private List<TaskFlowStatusItemVO> statusList;
}