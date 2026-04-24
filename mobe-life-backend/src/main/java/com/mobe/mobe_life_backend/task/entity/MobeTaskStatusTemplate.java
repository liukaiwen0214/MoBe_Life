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

@Schema(description = "状态流程模板表")
@Data
@TableName("mobe_task_status_template")
public class MobeTaskStatusTemplate {

  @Schema(description = "主键ID")
  @TableId(type = IdType.AUTO)
  private Long id;

  @Schema(description = "所属用户ID")
  private Long userId;

  @Schema(description = "模板名称")
  private String templateName;

  @Schema(description = "模板说明")
  private String templateDesc;

  @Schema(description = "执行模式：FREE-自由模式 FLOW-流程模式")
  private String executionMode;

  @Schema(description = "是否默认模板：0-否 1-是")
  private Integer isDefault;

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
