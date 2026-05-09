/**
 * 作为财务分类持久化模型，维护收入和支出的用户自定义分类树。
 * 模块：财务 / 分类管理。
 * 约束：同一用户下分类名称与层级需要保持可读性，避免记账入口出现重复或歧义选项。
 */
package com.mobe.mobe_life_backend.finance.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("mobe_finance_category")
@Schema(description = "财务分类表")
public class MobeFinanceCategory {

  @TableId(type = IdType.AUTO)
  @Schema(description = "主键ID")
  private Long id;

  @Schema(description = "所属用户ID")
  private Long userId;

  @Schema(description = "分类名称")
  private String categoryName;

  /** 分类归属方向，决定它只能用于支出账单或收入账单。 */
  @Schema(description = "分类类型：EXPENSE/INCOME")
  private String categoryType;

  /** 父分类引用；为空时表示一级分类，用于构建两级或多级分类树。 */
  @Schema(description = "父分类ID，一级分类时为空")
  private Long parentId;

  /** 前端展示图标的稳定编码，不直接保存图标资源本身。 */
  @Schema(description = "图标标识")
  private String iconCode;

  @Schema(description = "颜色标识")
  private String colorCode;

  @Schema(description = "排序值")
  private Integer sortNo;

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