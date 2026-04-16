package com.mobe.mobe_life_backend.task.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;

@Schema(description = "状态流转关系表")
@Data
@TableName("mobe_task_status_transition")
public class MobeTaskStatusTransition {

  @Schema(description = "主键ID")
  @TableId(type = IdType.AUTO)
  private Long id;

  @Schema(description = "所属用户ID")
  private Long userId;

  @Schema(description = "状态流程模板ID")
  private Long templateId;

  @Schema(description = "起始状态ID")
  private Long fromStatusId;

  @Schema(description = "目标状态ID")
  private Long toStatusId;

  @Schema(description = "排序值")
  private Integer sortNo;

  @Schema(description = "是否启用：0-否 1-是")
  private Integer isEnabled;

  @Schema(description = "创建人")
  private Long createdBy;

  @Schema(description = "最后修改人")
  private Long updatedBy;

  @Schema(description = "创建时间")
  @TableField(fill = FieldFill.INSERT)
  private LocalDateTime createTime;

  @Schema(description = "更新时间")
  @TableField(fill = FieldFill.INSERT_UPDATE)
  private LocalDateTime updateTime;

  @Schema(description = "是否逻辑删除：0-否 1-是")
  private Integer isDeleted;
}
