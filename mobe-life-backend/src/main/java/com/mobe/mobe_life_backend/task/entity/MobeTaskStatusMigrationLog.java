package com.mobe.mobe_life_backend.task.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;

@Schema(description = "状态迁移日志表")
@Data
@TableName("mobe_task_status_migration_log")
public class MobeTaskStatusMigrationLog {

  @Schema(description = "主键ID")
  @TableId(type = IdType.AUTO)
  private Long id;

  @Schema(description = "所属用户ID")
  private Long userId;

  @Schema(description = "旧模板ID")
  private Long oldTemplateId;

  @Schema(description = "新模板ID")
  private Long newTemplateId;

  @Schema(description = "状态映射关系JSON")
  private String migrationMappingJson;

  @Schema(description = "迁移说明")
  private String migrationDesc;

  @Schema(description = "受影响待办数量")
  private Integer affectedTaskCount;

  @Schema(description = "操作人ID")
  private Long operatorId;

  @Schema(description = "迁移时间")
  @TableField(fill = FieldFill.INSERT)
  private LocalDateTime createTime;

  @Schema(description = "是否逻辑删除：0-否 1-是")
  private Integer isDeleted;
}
