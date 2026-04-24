/**
 * 核心职责：封装目标中心的请求参数模型，承载控制层到服务层的输入数据。
 * 所属业务模块：目标中心 / DTO。
 * 重要依赖关系或外部约束：通常与参数校验注解配合使用，字段命名需要与接口语义保持一致。
 */
package com.mobe.mobe_life_backend.goal.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "目标列表查询参数")
public class GoalListQueryDTO {

  @Schema(description = "页码")
  private Integer pageNum;

  @Schema(description = "每页数量")
  private Integer pageSize;

  @Schema(description = "关键词")
  private String keyword;

  @Schema(description = "是否包含已完成目标")
  private Boolean includeCompleted;
}