package com.mobe.mobe_life_backend.finance.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("mobe_finance_recurring_bill")
@Schema(description = "固定收支表")
public class MobeFinanceRecurringBill {

  @TableId(type = IdType.AUTO)
  @Schema(description = "主键ID")
  private Long id;

  @Schema(description = "所属用户ID")
  private Long userId;

  @Schema(description = "固定收支名称")
  private String recurringName;

  @Schema(description = "类型：EXPENSE/INCOME")
  private String billType;

  @Schema(description = "金额")
  private BigDecimal amount;

  @Schema(description = "分类ID")
  private Long categoryId;

  @Schema(description = "账户ID")
  private Long accountId;

  @Schema(description = "周期类型：DAILY/WEEKLY/MONTHLY")
  private String cycleType;

  @Schema(description = "周期值，例如每月5号、每周一")
  private String cycleValue;

  @Schema(description = "生效开始日期")
  private LocalDate startDate;

  @Schema(description = "生效结束日期")
  private LocalDate endDate;

  @Schema(description = "下次执行日期")
  private LocalDate nextExecuteDate;

  @Schema(description = "上次执行时间")
  private LocalDateTime lastExecuteTime;

  @Schema(description = "是否自动生成账单：0-否 1-是")
  private Integer autoGenerate;

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