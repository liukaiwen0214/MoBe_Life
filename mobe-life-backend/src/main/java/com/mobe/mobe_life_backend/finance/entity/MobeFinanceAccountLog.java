/**
 * 作为账户余额变动日志持久化模型，记录每次余额变更前后的完整快照。
 * 模块：财务 / 账户审计。
 * 约束：日志应追加写入，不应被业务更新覆盖，用于对账、回滚和问题追踪。
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
@TableName("mobe_finance_account_log")
@Schema(description = "账户余额变动日志表")
public class MobeFinanceAccountLog {

  @TableId(type = IdType.AUTO)
  @Schema(description = "主键ID")
  private Long id;

  @Schema(description = "所属用户ID")
  private Long userId;

  @Schema(description = "账户ID")
  private Long accountId;

  /** 触发余额变化的业务类型，用于区分账单、转账和手动调账。 */
  @Schema(description = "业务类型：BILL/TRANSFER/ADJUST")
  private String bizType;

  /** 触发余额变化的业务主键，用于回查原始操作记录。 */
  @Schema(description = "业务ID")
  private Long bizId;

  /** 余额变化方向，IN 增加余额，OUT 减少余额。 */
  @Schema(description = "变动类型：IN/OUT")
  private String changeType;

  /** 变更前账户余额快照，用于审计和回滚计算。 */
  @Schema(description = "变动前余额")
  private BigDecimal beforeBalance;

  /** 本次变更金额，始终为正数，方向由 changeType 表达。 */
  @Schema(description = "变动金额")
  private BigDecimal changeAmount;

  /** 变更后账户余额快照，应等于 beforeBalance 按方向叠加 changeAmount 后的结果。 */
  @Schema(description = "变动后余额")
  private BigDecimal afterBalance;

  @Schema(description = "备注")
  private String remark;

  @TableField(fill = FieldFill.INSERT)
  @Schema(description = "创建时间")
  private LocalDateTime createTime;

  @Schema(description = "是否逻辑删除：0-否 1-是")
  private Integer isDeleted;
}