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

  @Schema(description = "预算类型：TOTAL/CATEGORY")
  private String budgetType;

  @Schema(description = "分类ID，分类预算时使用")
  private Long categoryId;

  @Schema(description = "周期类型：MONTH")
  private String periodType;

  @Schema(description = "周期标识，例如 2026-04")
  private String periodKey;

  @Schema(description = "预算金额")
  private BigDecimal budgetAmount;

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