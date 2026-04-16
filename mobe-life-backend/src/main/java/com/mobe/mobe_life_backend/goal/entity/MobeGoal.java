package com.mobe.mobe_life_backend.goal.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;

@Schema(description = "目标实体对象")
@Data
@TableName("mobe_goal")
public class MobeGoal {

  @Schema(description = "目标ID")
  @TableId(type = IdType.AUTO)
  private Long id;

  @Schema(description = "用户ID")
  private Long userId;

  @Schema(description = "目标业务编号")
  private String goalNo;

  @Schema(description = "目标标题")
  private String title;

  @Schema(description = "目标内容")
  private String content;

  @Schema(description = "备注")
  private String remark;

  @Schema(description = "状态模板ID")
  private Long statusTemplateId;

  @Schema(description = "当前状态ID")
  private Long currentStatusId;

  @Schema(description = "执行模式")
  private String executionMode;

  @Schema(description = "排序值")
  private Integer sortNo;

  @Schema(description = "是否归档：0-否，1-是")
  private Integer isArchived;

  @Schema(description = "归档时间")
  private LocalDateTime archivedAt;

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