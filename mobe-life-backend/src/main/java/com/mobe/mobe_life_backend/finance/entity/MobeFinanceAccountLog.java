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

  @Schema(description = "业务类型：BILL/TRANSFER/ADJUST")
  private String bizType;

  @Schema(description = "业务ID")
  private Long bizId;

  @Schema(description = "变动类型：IN/OUT")
  private String changeType;

  @Schema(description = "变动前余额")
  private BigDecimal beforeBalance;

  @Schema(description = "变动金额")
  private BigDecimal changeAmount;

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