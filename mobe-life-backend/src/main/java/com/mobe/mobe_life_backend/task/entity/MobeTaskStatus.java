package com.mobe.mobe_life_backend.task.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;

@Schema(description = "状态表")
@Data
@TableName("mobe_task_status")
public class MobeTaskStatus {

  @Schema(description = "主键ID")
  @TableId(type = IdType.AUTO)
  private Long id;

  @Schema(description = "所属用户ID")
  private Long userId;

  @Schema(description = "状态流程模板ID")
  private Long templateId;

  @Schema(description = "状态名称")
  private String statusName;

  @Schema(description = "状态编码标识")
  private String statusCode;

  @Schema(description = "状态说明")
  private String statusDesc;

  @Schema(description = "状态图标标识")
  private String statusIcon;

  @Schema(description = "状态颜色标识")
  private String statusColor;

  @Schema(description = "排序值")
  private Integer sortNo;

  @Schema(description = "是否初始状态：0-否 1-是")
  private Integer isInitial;

  @Schema(description = "是否结束状态：0-否 1-是")
  private Integer isTerminal;

  @Schema(description = "是否启用：0-否 1-是")
  private Integer isEnabled;

  @Schema(description = "进入该状态时是否自动记录实际开始时间：0-否 1-是")
  private Integer recordActualStartTime;

  @Schema(description = "进入该状态时是否自动记录完成时间：0-否 1-是")
  private Integer recordCompletedTime;

  @Schema(description = "是否允许从结束状态跳出/恢复：0-否 1-是")
  private Integer allowReopen;

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
