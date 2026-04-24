/**
 * 核心职责：封装项目中心的请求参数模型，承载控制层到服务层的输入数据。
 * 所属业务模块：项目中心 / DTO。
 * 重要依赖关系或外部约束：通常与参数校验注解配合使用，字段命名需要与接口语义保持一致。
 */
package com.mobe.mobe_life_backend.project.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 项目列表查询DTO
 */
@Data
@Schema(description = "项目列表查询参数")
public class ProjectListQueryDTO {

  @Schema(description = "页码，默认1", example = "1")
  private Integer pageNum;

  @Schema(description = "每页大小，默认10", example = "10")
  private Integer pageSize;

  @Schema(description = "搜索关键词", example = "项目")
  private String keyword;

  // @Schema(description = "状态编码", example = "ACTIVE")
  // private String statusCode;
  @Schema(description = "是否包含已完成项目")
  private Boolean includeCompleted;
}
