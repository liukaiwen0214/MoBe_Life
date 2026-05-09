/**
 * 作为财务账单持久化模型，承载收入、支出和转账拆分后的单边记录。
 * 模块：财务 / 账单流水。
 * 约束：转账必须通过 transferGroupNo 关联两条方向相反的账单，避免单条记录同时表达双方账户。
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
@TableName("mobe_finance_bill")
@Schema(description = "财务账单表")
public class MobeFinanceBill {

  @TableId(type = IdType.AUTO)
  @Schema(description = "主键ID")
  private Long id;

  @Schema(description = "所属用户ID")
  private Long userId;

  /** 业务侧唯一账单编号，便于幂等创建、问题排查和对外引用。 */
  @Schema(description = "账单编号，唯一")
  private String billNo;

  /** 账单方向和语义类型，转账会拆分为转出与转入两种方向。 */
  @Schema(description = "账单类型：EXPENSE/INCOME/TRANSFER_OUT/TRANSFER_IN")
  private String billType;

  /** 同一次转账的分组号，用来把转出记录和转入记录重新组装为一个业务动作。 */
  @Schema(description = "转账分组编号，同一次转账的两条记录共用")
  private String transferGroupNo;

  /** 账单金额，始终存储正数，方向由 billType 和 changeType 表达。 */
  @Schema(description = "金额")
  private BigDecimal amount;

  @Schema(description = "分类ID")
  private Long categoryId;

  /** 当前流水实际影响的账户，收入增加余额、支出减少余额。 */
  @Schema(description = "当前账单所属账户ID")
  private Long accountId;

  /** 转账场景下的对端账户，普通收支场景可以为空。 */
  @Schema(description = "关联账户ID，转账时表示对端账户")
  private Long relatedAccountId;

  /** 用户选择的业务发生时间，不等同于记录创建时间。 */
  @Schema(description = "账单时间")
  private LocalDateTime billDate;

  @Schema(description = "账单标题")
  private String title;

  @Schema(description = "详细内容")
  private String content;

  @Schema(description = "备注")
  private String remark;

  @Schema(description = "关联项目ID")
  private Long relatedProjectId;

  @Schema(description = "关联目标ID")
  private Long relatedGoalId;

  @Schema(description = "关联节点ID")
  private Long relatedNodeId;

  @Schema(description = "来源类型：MANUAL/FIXED/AUTO")
  private String sourceType;

  @Schema(description = "来源记录ID，例如固定收支模板ID")
  private Long sourceId;

  @Schema(description = "记录状态：ACTIVE/VOID")
  private String recordStatus;

  @TableField(fill = FieldFill.INSERT)
  @Schema(description = "创建时间")
  private LocalDateTime createTime;

  @TableField(fill = FieldFill.INSERT_UPDATE)
  @Schema(description = "更新时间")
  private LocalDateTime updateTime;

  @Schema(description = "是否逻辑删除：0-否 1-是")
  private Integer isDeleted;
}