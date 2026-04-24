/**
 * 核心职责：封装待办中心的请求参数模型，承载控制层到服务层的输入数据。
 * 所属业务模块：待办中心 / DTO。
 * 重要依赖关系或外部约束：通常与参数校验注解配合使用，字段命名需要与接口语义保持一致。
 */
package com.mobe.mobe_life_backend.task.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@Schema(description = "新增待办请求")
public class TaskCreateDTO {

  @Schema(description = "待办标题", requiredMode = Schema.RequiredMode.REQUIRED)
  private String title;

  @Schema(description = "待办内容")
  private String content;

  @Schema(description = "备注")
  private String remark;

  @Schema(description = "直属归属类型：INDEPENDENT/PROJECT/GOAL/NODE", requiredMode = Schema.RequiredMode.REQUIRED)
  private String directOwnerType;

  @Schema(description = "直属归属对象ID，独立待办时可为空")
  private Long directOwnerId;

  @Schema(description = "计划开始时间")
  private LocalDateTime planStartTime;

  @Schema(description = "计划结束时间")
  private LocalDateTime planEndTime;

  @Schema(description = "截止时间")
  private LocalDateTime deadlineTime;
}