/**
 * 核心职责：封装节点中心返回给前端或上层调用方的展示模型。
 * 所属业务模块：节点中心 / VO。
 * 重要依赖关系或外部约束：字段设计更偏向展示与接口契约，不一定与数据库结构一一对应。
 */
package com.mobe.mobe_life_backend.node.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@Schema(description = "节点详情-待办项")
public class NodeTaskItemVO {

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
}