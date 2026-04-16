package com.mobe.mobe_life_backend.task.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;

@Schema(description = "批量操作日志表")
@Data
@TableName("mobe_task_batch_operation_log")
public class MobeTaskBatchOperationLog {

  @Schema(description = "主键ID")
  @TableId(type = IdType.AUTO)
  private Long id;

  @Schema(description = "所属用户ID")
  private Long userId;

  @Schema(description = "批量操作类型：STATUS_CHANGE/DELETE/ARCHIVE/RESTORE/OWNER_CHANGE")
  private String batchOperationType;

  @Schema(description = "操作说明")
  private String operationDesc;

  @Schema(description = "操作对象数量")
  private Integer targetCount;

  @Schema(description = "操作对象ID列表JSON")
  private String targetIdsJson;

  @Schema(description = "变更前摘要JSON")
  private String beforeValueJson;

  @Schema(description = "变更后摘要JSON")
  private String afterValueJson;

  @Schema(description = "操作人ID")
  private Long operatorId;

  @Schema(description = "操作时间")
  @TableField(fill = FieldFill.INSERT)
  private LocalDateTime createTime;

  @Schema(description = "是否逻辑删除：0-否 1-是")
  private Integer isDeleted;
}
