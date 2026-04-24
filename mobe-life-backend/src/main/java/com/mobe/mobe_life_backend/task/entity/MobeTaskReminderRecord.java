/**
 * 核心职责：定义待办中心的数据实体，用于映射数据库记录或领域状态。
 * 所属业务模块：待办中心 / 实体模型。
 * 重要依赖关系或外部约束：字段通常需要与数据库表结构、MyBatis-Plus 映射约定保持一致。
 */
package com.mobe.mobe_life_backend.task.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;

@Schema(description = "提醒触发记录表")
@Data
@TableName("mobe_task_reminder_record")
public class MobeTaskReminderRecord {

  @Schema(description = "主键ID")
  @TableId(type = IdType.AUTO)
  private Long id;

  @Schema(description = "所属用户ID")
  private Long userId;

  @Schema(description = "提醒ID")
  private Long reminderId;

  @Schema(description = "待办ID")
  private Long taskItemId;

  @Schema(description = "计划提醒时间")
  private LocalDateTime plannedRemindAt;

  @Schema(description = "实际提醒时间")
  private LocalDateTime actualRemindAt;

  @Schema(description = "触发结果：SUCCESS/FAILED/SKIPPED")
  private String triggerResult;

  @Schema(description = "阅读状态：UNREAD/READ")
  private String readStatus;

  @Schema(description = "处理状态：UNPROCESSED/PROCESSED/SNOOZED")
  private String handleStatus;

  @Schema(description = "处理说明")
  private String handleDesc;

  @Schema(description = "创建时间")
  @TableField(fill = FieldFill.INSERT)
  private LocalDateTime createTime;

  @Schema(description = "是否逻辑删除：0-否 1-是")
  private Integer isDeleted;
}
