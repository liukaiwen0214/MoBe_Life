/**
 * 作为财务账户持久化模型，记录用户现金、银行卡、第三方钱包等资产容器。
 * 模块：财务 / 账户管理。
 * 约束：余额字段必须使用 BigDecimal，避免金额计算出现浮点精度问题。
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
@TableName("mobe_finance_account")
@Schema(description = "财务账户表")
public class MobeFinanceAccount {

  /**
   * 数据库自增主键。
   * 说明：仅用于内部关联，不作为对外展示的账户编号。
   */
  @TableId(type = IdType.AUTO)
  @Schema(description = "主键ID")
  private Long id;

  /** 用户维度隔离字段，所有账户查询都必须带上该条件。 */
  @Schema(description = "所属用户ID")
  private Long userId;

  /** 用户自定义的账户名称，例如“招商储蓄卡”或“微信零钱”。 */
  @Schema(description = "账户名称")
  private String accountName;

  /** 账户类型枚举文本，驱动前端分组、图标和业务规则。 */
  @Schema(description = "账户类型：CASH/WECHAT/ALIPAY/BANK/CREDIT_CARD/OTHER")
  private String accountType;

  /** 创建账户时录入的起始余额，用于后续资产追溯。 */
  @Schema(description = "初始余额")
  private BigDecimal initialBalance;

  /** 当前可用余额，由账单、转账和余额调整共同维护。 */
  @Schema(description = "当前余额")
  private BigDecimal currentBalance;

  /** 是否纳入资产统计，信用卡或临时账户可选择不计入。 */
  @Schema(description = "是否计入总资产：0-否 1-是")
  private Integer includeInAsset;

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