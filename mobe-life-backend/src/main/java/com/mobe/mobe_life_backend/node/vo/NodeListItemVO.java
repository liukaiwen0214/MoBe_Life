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
@Schema(description = "节点列表项")
public class NodeListItemVO {

  @Schema(description = "节点ID")
  private Long id;

  @Schema(description = "节点标题")
  private String title;

  @Schema(description = "所属类型")
  private String ownerType;

  @Schema(description = "所属对象ID")
  private Long ownerId;

  @Schema(description = "所属对象名称")
  private String ownerName;

  @Schema(description = "待办数量")
  private Integer taskCount;

  @Schema(description = "更新时间")
  private LocalDateTime updateTime;
}