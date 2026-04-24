/**
 * 核心职责：封装项目中心返回给前端或上层调用方的展示模型。
 * 所属业务模块：项目中心 / VO。
 * 重要依赖关系或外部约束：字段设计更偏向展示与接口契约，不一定与数据库结构一一对应。
 */
package com.mobe.mobe_life_backend.project.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

import lombok.Data;

/**
 * 项目列表项VO
 */
@Data
@Schema(description = "项目列表项信息")
public class ProjectListItemVO {

  @Schema(description = "项目ID", example = "1")
  private Long id;

  @Schema(description = "项目标题", example = "个人项目")
  private String title;

  @Schema(description = "节点数量", example = "5")
  private Integer nodeCount;

  @Schema(description = "任务数量", example = "10")
  private Integer taskCount;

  @Schema(description = "已完成任务数量", example = "3")
  private Integer completedCount;

  @Schema(description = "总任务数量", example = "10")
  private Integer totalCount;

  // @Schema(description = "状态编码", example = "ACTIVE")
  // private String statusCode;

  // @Schema(description = "状态文本", example = "进行中")
  // private String statusText;

  @Schema(description = "更新时间", example = "2026-04-16T13:00:00")
  private LocalDateTime updateTime;
  @Schema(description = "是否已完成：0-未完成 1-已完成")
  private Integer isCompleted;

  @Schema(description = "完成时间")
  private LocalDateTime completedTime;
}
