package com.mobe.mobe_life_backend.task.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;

@Schema(description = "重复任务主表")
@Data
@TableName("mobe_task_repeat_task")
public class MobeTaskRepeatTask {

  @Schema(description = "主键ID")
  @TableId(type = IdType.AUTO)
  private Long id;

  @Schema(description = "所属用户ID")
  private Long userId;

  @Schema(description = "重复任务名称")
  private String repeatName;

  @Schema(description = "详细内容")
  private String content;

  @Schema(description = "备注")
  private String remark;

  @Schema(description = "重复类型：DAILY/WEEKLY/MONTHLY/YEARLY/WORKDAY/FIXED_DATE/CUSTOM")
  private String repeatType;

  @Schema(description = "重复规则JSON")
  private String repeatRuleJson;

  @Schema(description = "生效开始时间")
  private LocalDateTime effectiveStartTime;

  @Schema(description = "结束类型：MANUAL/DATE/COUNT/INFINITE")
  private String endType;

  @Schema(description = "按日期结束时的结束时间")
  private LocalDateTime endDate;

  @Schema(description = "按次数结束时的结束次数")
  private Integer endCount;

  @Schema(description = "已生成实例数量")
  private Integer generatedCount;

  @Schema(description = "默认提醒配置JSON")
  private String defaultReminderJson;

  @Schema(description = "是否启用：0-否 1-是")
  private Integer isEnabled;

  @Schema(description = "是否暂停：0-否 1-是")
  private Integer isPaused;

  @Schema(description = "手动停止时间")
  private LocalDateTime stoppedAt;

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
