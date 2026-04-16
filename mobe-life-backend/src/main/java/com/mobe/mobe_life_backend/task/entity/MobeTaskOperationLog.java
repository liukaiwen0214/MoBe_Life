package com.mobe.mobe_life_backend.task.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;

@Schema(description = "待办操作日志表")
@Data
@TableName("mobe_task_operation_log")
public class MobeTaskOperationLog {

  @Schema(description = "主键ID")
  @TableId(type = IdType.AUTO)
  private Long id;

  @Schema(description = "所属用户ID")
  private Long userId;

  @Schema(description = "待办ID")
  private Long taskItemId;

  @Schema(description = "操作类型：CREATE/UPDATE/STATUS_CHANGE/TIME_CHANGE/OWNER_CHANGE/COMPLETE/REOPEN/ARCHIVE/RESTORE/DELETE")
  private String operationType;

  @Schema(description = "操作说明")
  private String operationDesc;

  @Schema(description = "变更前数据JSON")
  private String beforeValueJson;

  @Schema(description = "变更后数据JSON")
  private String afterValueJson;

  @Schema(description = "操作人ID")
  private Long operatorId;

  @Schema(description = "操作时间")
  @TableField(fill = FieldFill.INSERT)
  private LocalDateTime createTime;

  @Schema(description = "是否逻辑删除：0-否 1-是")
  private Integer isDeleted;
}
