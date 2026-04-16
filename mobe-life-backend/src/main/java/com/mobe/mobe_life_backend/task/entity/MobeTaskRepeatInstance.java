package com.mobe.mobe_life_backend.task.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;

@Schema(description = "重复任务实例关系表")
@Data
@TableName("mobe_task_repeat_instance")
public class MobeTaskRepeatInstance {

  @Schema(description = "主键ID")
  @TableId(type = IdType.AUTO)
  private Long id;

  @Schema(description = "所属用户ID")
  private Long userId;

  @Schema(description = "重复任务ID")
  private Long repeatTaskId;

  @Schema(description = "对应待办主表ID")
  private Long taskItemId;

  @Schema(description = "实例归属日期")
  private LocalDate instanceDate;

  @Schema(description = "实例序号")
  private Integer instanceNo;

  @Schema(description = "修改范围：CURRENT/FUTURE")
  private String modifyScope;

  @Schema(description = "创建时间")
  @TableField(fill = FieldFill.INSERT)
  private LocalDateTime createTime;

  @Schema(description = "更新时间")
  @TableField(fill = FieldFill.INSERT_UPDATE)
  private LocalDateTime updateTime;

  @Schema(description = "是否逻辑删除：0-否 1-是")
  private Integer isDeleted;
}
