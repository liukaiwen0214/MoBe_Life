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
  
  @Schema(description = "状态编码", example = "ACTIVE")
  private String statusCode;
}
