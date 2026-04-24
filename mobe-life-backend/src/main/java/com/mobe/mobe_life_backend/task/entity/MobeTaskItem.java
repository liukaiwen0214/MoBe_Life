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

@Schema(description = "待办主表")
@Data
@TableName("mobe_task_item")
public class MobeTaskItem {

  @Schema(description = "主键ID")
  @TableId(type = IdType.AUTO)
  private Long id;

  @Schema(description = "所属用户ID")
  private Long userId;

  @Schema(description = "待办编号")
  private String taskNo;

  @Schema(description = "标题")
  private String title;

  @Schema(description = "详细内容")
  private String content;

  @Schema(description = "备注")
  private String remark;

  @Schema(description = "直接归属类型：GOAL/PROJECT/NODE/INDEPENDENT")
  private String directOwnerType;

  @Schema(description = "直接归属ID，独立待办时为空")
  private Long directOwnerId;

  @Schema(description = "状态流程模板ID")
  private Long statusTemplateId;

  @Schema(description = "当前状态ID")
  private Long currentStatusId;

  @Schema(description = "计划开始时间")
  private LocalDateTime planStartTime;

  @Schema(description = "计划结束时间")
  private LocalDateTime planEndTime;

  @Schema(description = "截止时间")
  private LocalDateTime deadlineTime;

  @Schema(description = "实际开始时间")
  private LocalDateTime actualStartTime;

  @Schema(description = "完成时间")
  private LocalDateTime completedAt;

  @Schema(description = "归档时间")
  private LocalDateTime archivedAt;

  @Schema(description = "记录状态：ACTIVE/ARCHIVED/DELETED")
  private String recordStatus;

  @Schema(description = "优先级：P0/P1/P2/P3")
  private String priorityLevel;

  @Schema(description = "紧急度：HIGH/MEDIUM/LOW")
  private String urgencyLevel;

  @Schema(description = "紧急度来源：AUTO/MANUAL")
  private String urgencySource;

  @Schema(description = "排序值")
  private Integer sortNo;

  @Schema(description = "是否置顶：0-否 1-是")
  private Integer isPinned;

  @Schema(description = "是否关注：0-否 1-是")
  private Integer isFocused;

  @Schema(description = "颜色标识")
  private String colorCode;

  @Schema(description = "图标标识")
  private String iconCode;

  @Schema(description = "卡片样式标记")
  private String cardStyleCode;

  @Schema(description = "来源类型：MANUAL/REPEAT/BATCH/OTHER")
  private String sourceType;

  @Schema(description = "来源渠道")
  private String sourceChannel;

  @Schema(description = "是否由重复任务生成：0-否 1-是")
  private Integer isRepeatGenerated;

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
