/**
 * 核心职责：封装项目中心返回给前端或上层调用方的展示模型。
 * 所属业务模块：项目中心 / VO。
 * 重要依赖关系或外部约束：字段设计更偏向展示与接口契约，不一定与数据库结构一一对应。
 */
package com.mobe.mobe_life_backend.project.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
@Schema(description = "项目详情")
public class ProjectDetailVO {

  @Schema(description = "项目ID")
  private Long id;

  @Schema(description = "项目标题")
  private String title;

  @Schema(description = "项目内容")
  private String content;

  @Schema(description = "备注")
  private String remark;

  @Schema(description = "执行模式")
  private String executionMode;

  @Schema(description = "状态模板ID")
  private Long statusTemplateId;

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
  private List<ProjectNodeItemVO> nodes;

  @Schema(description = "待办列表")
  private List<ProjectTaskItemVO> tasks;

  @Schema(description = "状态列表")
  private List<ProjectStatusItemVO> statusList;

  @Schema(description = "执行日志")
  private List<ProjectLogItemVO> logs;
  @Schema(description = "是否已完成：0-未完成 1-已完成")
  private Integer isCompleted;

  @Schema(description = "完成时间")
  private LocalDateTime completedTime;
}