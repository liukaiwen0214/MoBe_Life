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

  @TableId(type = IdType.AUTO)
  @Schema(description = "主键ID")
  private Long id;

  @Schema(description = "所属用户ID")
  private Long userId;

  @Schema(description = "账户名称")
  private String accountName;

  @Schema(description = "账户类型：CASH/WECHAT/ALIPAY/BANK/CREDIT_CARD/OTHER")
  private String accountType;

  @Schema(description = "初始余额")
  private BigDecimal initialBalance;

  @Schema(description = "当前余额")
  private BigDecimal currentBalance;

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