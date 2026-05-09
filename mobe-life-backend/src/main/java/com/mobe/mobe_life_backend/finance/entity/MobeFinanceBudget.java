/**
 * 作为预算持久化模型，记录用户在指定周期内的总预算或分类预算。
 * 模块：财务 / 预算管理。
 * 约束：预算周期通过 periodType 与 periodKey 组合表达，方便按月聚合和后续扩展周、年周期。
 */
package com.mobe.mobe_life_backend.finance.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("mobe_finance_budget")
@Schema(description = "财务预算表")
public class MobeFinanceBudget {

  @TableId(type = IdType.AUTO)
  @Schema(description = "主键ID")
  private Long id;

  @Schema(description = "所属用户ID")
  private Long userId;

  @Schema(description = "预算名称")
  private String budgetName;

  /** 预算覆盖范围；TOTAL 表示全局预算，CATEGORY 表示单个分类预算。 */
  @Schema(description = "预算类型：TOTAL/CATEGORY")
  private String budgetType;

  /** 分类预算绑定的分类 ID，总预算场景下为空。 */
  @Schema(description = "分类ID，分类预算时使用")
  private Long categoryId;

  /** 周期粒度，目前按月控制，字段保留扩展空间。 */
  @Schema(description = "周期类型：MONTH")
  private String periodType;

  /** 周期键，例如 2026-04，用字符串降低跨数据库日期格式差异。 */
  @Schema(description = "周期标识，例如 2026-04")
  private String periodKey;

  /** 预算上限金额，用于与周期内实际支出累计值比较。 */
  @Schema(description = "预算金额")
  private BigDecimal budgetAmount;

  /** 预警触发比例，例如 0.80 表示使用达到 80% 时提示。 */
  @Schema(description = "预警比例，例如 0.80")
  private BigDecimal warningRatio;

  @Schema(description = "是否启用：0-否 1-是")
  private Integer isEnabled;

  @Schema(description = "备注")
  private String remark;

  @TableField(fill = FieldFill.INSERT)
  @Schema(description = "创建时间")
  private LocalDateTime createTime;

  @TableField(fill = FieldFill.INSERT_UPDATE)
  @Schema(description = "更新时间")
  private LocalDateTime updateTime;

  @Schema(description = "是否逻辑删除：0-否 1-是")
  private Integer isDeleted;
}