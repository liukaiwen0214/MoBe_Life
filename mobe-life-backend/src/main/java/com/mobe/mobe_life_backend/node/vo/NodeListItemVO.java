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