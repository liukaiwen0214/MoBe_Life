package com.mobe.mobe_life_backend.node.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "节点列表查询参数")
public class NodeListQueryDTO {

  @Schema(description = "页码")
  private Integer pageNum;

  @Schema(description = "每页数量")
  private Integer pageSize;

  @Schema(description = "关键词")
  private String keyword;

  @Schema(description = "所属类型：PROJECT/GOAL")
  private String ownerType;
}