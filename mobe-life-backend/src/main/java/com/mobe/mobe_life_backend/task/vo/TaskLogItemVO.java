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
@Schema(description = "待办详情-日志项")
public class TaskLogItemVO {

  @Schema(description = "日志ID")
  private Long id;

  @Schema(description = "日志类型")
  private String logType;

  @Schema(description = "日志描述")
  private String logText;

  @Schema(description = "创建时间")
  private LocalDateTime createTime;
}