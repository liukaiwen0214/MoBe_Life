package com.mobe.mobe_life_backend.node.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;

@Schema(description = "节点实体对象")
@Data
@TableName("mobe_node")
public class MobeNode {

  @Schema(description = "节点ID")
  @TableId(type = IdType.AUTO)
  private Long id;

  @Schema(description = "用户ID")
  private Long userId;

  @Schema(description = "节点业务编号")
  private String nodeNo;

  @Schema(description = "节点标题")
  private String title;

  @Schema(description = "节点内容")
  private String content;

  @Schema(description = "备注")
  private String remark;

  @Schema(description = "所属类型：PROJECT/GOAL")
  private String ownerType;

  @Schema(description = "所属项目或目标ID")
  private Long ownerId;

  @Schema(description = "排序值")
  private Integer sortNo;

  @Schema(description = "创建人")
  private Long createdBy;

  @Schema(description = "更新人")
  private Long updatedBy;

  @Schema(description = "创建时间")
  @TableField(fill = FieldFill.INSERT)
  private LocalDateTime createTime;

  @Schema(description = "更新时间")
  @TableField(fill = FieldFill.INSERT_UPDATE)
  private LocalDateTime updateTime;

  @Schema(description = "逻辑删除标记，0-未删除，1-已删除")
  private Integer isDeleted;
}