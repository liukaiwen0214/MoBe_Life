package com.mobe.mobe_life_backend.node.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
@Schema(description = "节点详情")
public class NodeDetailVO {

  @Schema(description = "节点ID")
  private Long id;

  @Schema(description = "节点标题")
  private String title;

  @Schema(description = "节点内容")
  private String content;

  @Schema(description = "备注")
  private String remark;

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

  @Schema(description = "待办列表")
  private List<NodeTaskItemVO> tasks;
}