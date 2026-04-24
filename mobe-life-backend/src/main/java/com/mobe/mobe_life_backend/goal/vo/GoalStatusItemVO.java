/**
 * 核心职责：封装目标中心返回给前端或上层调用方的展示模型。
 * 所属业务模块：目标中心 / VO。
 * 重要依赖关系或外部约束：字段设计更偏向展示与接口契约，不一定与数据库结构一一对应。
 */
package com.mobe.mobe_life_backend.goal.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "目标详情-状态项")
public class GoalStatusItemVO {

  @Schema(description = "状态ID")
  private Long id;

  @Schema(description = "状态名称")
  private String statusName;

  @Schema(description = "状态编码")
  private String statusCode;

  @Schema(description = "是否初始状态")
  private Integer isInitial;

  @Schema(description = "是否结束状态")
  private Integer isTerminal;

  @Schema(description = "是否启用")
  private Integer isEnabled;
}