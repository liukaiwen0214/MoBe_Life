package com.mobe.mobe_life_backend.task.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;

@Schema(description = "待办提醒表")
@Data
@TableName("mobe_task_reminder")
public class MobeTaskReminder {

  @Schema(description = "主键ID")
  @TableId(type = IdType.AUTO)
  private Long id;

  @Schema(description = "所属用户ID")
  private Long userId;

  @Schema(description = "待办ID")
  private Long taskItemId;

  @Schema(description = "提醒类型：BEFORE_START/BEFORE_DEADLINE/SPECIFIED_TIME/DELAY/CONTINUOUS/SNOOZE")
  private String reminderType;

  @Schema(description = "指定提醒时间")
  private LocalDateTime reminderTime;

  @Schema(description = "相对提醒偏移分钟数")
  private Integer offsetMinutes;

  @Schema(description = "重复提醒间隔分钟数")
  private Integer repeatIntervalMinutes;

  @Schema(description = "下次提醒时间")
  private LocalDateTime nextRemindAt;

  @Schema(description = "上次提醒时间")
  private LocalDateTime lastRemindedAt;

  @Schema(description = "处理时间")
  private LocalDateTime processedAt;

  @Schema(description = "提醒状态：PENDING/TRIGGERED/PROCESSED/EXPIRED/DISABLED")
  private String reminderStatus;

  @Schema(description = "是否启用：0-否 1-是")
  private Integer isEnabled;

  @Schema(description = "是否手动覆盖：0-否 1-是")
  private Integer isManualOverride;

  @Schema(description = "是否失效：0-否 1-是")
  private Integer isExpired;

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
