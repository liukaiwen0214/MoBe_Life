/**
 * 核心职责：封装项目中心返回给前端或上层调用方的展示模型。
 * 所属业务模块：项目中心 / VO。
 * 重要依赖关系或外部约束：字段设计更偏向展示与接口契约，不一定与数据库结构一一对应。
 */
package com.mobe.mobe_life_backend.project.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "项目详情-节点项")
public class ProjectNodeItemVO {

  @Schema(description = "节点ID")
  private Long id;

  @Schema(description = "节点标题")
  private String title;

  @Schema(description = "节点下待办数量")
  private Integer taskCount;
}