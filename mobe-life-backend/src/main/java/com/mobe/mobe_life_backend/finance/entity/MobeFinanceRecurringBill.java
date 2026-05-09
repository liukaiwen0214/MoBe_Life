/**
 * 作为固定收支模板持久化模型，定义可周期性自动生成账单的规则。
 * 模块：财务 / 固定收支。
 * 约束：模板本身不代表真实账单，只有执行后生成的账单才会影响账户余额。
 */
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

  /** 重复规则的周期粒度，决定调度器如何计算下一次执行日期。 */
  @Schema(description = "周期类型：DAILY/WEEKLY/MONTHLY")
  private String cycleType;

  /** 周期参数文本，例如每月第几天或每周星期几，由业务层负责解析。 */
  @Schema(description = "周期值，例如每月5号、每周一")
  private String cycleValue;

  /** 模板开始生效的日期，早于该日期时调度器不应生成账单。 */
  @Schema(description = "生效开始日期")
  private LocalDate startDate;

  /** 模板结束生效的日期；为空表示长期有效。 */
  @Schema(description = "生效结束日期")
  private LocalDate endDate;

  /** 调度器下一次应尝试生成账单的日期，执行成功后会向后滚动。 */
  @Schema(description = "下次执行日期")
  private LocalDate nextExecuteDate;

  /** 最近一次成功执行时间，用于排查漏跑、重复执行和补偿任务。 */
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