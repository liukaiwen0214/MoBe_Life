/**
 * 核心职责：封装待办中心返回给前端或上层调用方的展示模型。
 * 所属业务模块：待办中心 / VO。
 * 重要依赖关系或外部约束：字段设计更偏向展示与接口契约，不一定与数据库结构一一对应。
 */
package com.mobe.mobe_life_backend.task.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@Schema(description = "待办列表项")
public class TaskListItemVO {

  @Schema(description = "待办ID")
  private Long id;

  @Schema(description = "待办标题")
  private String title;

  @Schema(description = "归属类型")
  private String directOwnerType;

  @Schema(description = "归属对象ID")
  private Long directOwnerId;

  @Schema(description = "归属对象名称")
  private String ownerName;

  @Schema(description = "状态编码")
  private String statusCode;

  @Schema(description = "状态名称")
  private String statusText;

  @Schema(description = "截止时间")
  private LocalDateTime deadlineTime;

  @Schema(description = "更新时间")
  private LocalDateTime updateTime;

  @Schema(description = "根归属类型")
  private String rootOwnerType;
  @Schema(description = "根归属对象名称")
  private String rootOwnerName;
  @Schema(description = "根节点名称")
  private String nodeName;
  @Schema(description = "状态颜色")
  private String statusColor;

  @Schema(description = "状态图标")
  private String statusIcon;

  @Schema(description = "是否初始状态")
  private Integer isInitial;

  @Schema(description = "是否结束状态")
  private Integer isTerminal;

  @Schema(description = "状态排序值")
  private Integer statusSortNo;
  @Schema(description = "待办颜色")
  private String colorCode;
}