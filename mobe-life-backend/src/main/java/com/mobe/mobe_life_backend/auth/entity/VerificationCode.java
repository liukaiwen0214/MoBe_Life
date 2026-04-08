package com.mobe.mobe_life_backend.auth.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("verification_code")
public class VerificationCode {

  @TableId(type = IdType.AUTO)
  private Long id;

  /**
   * 手机号或邮箱
   */
  private String target;

  /**
   * 1-手机号，2-邮箱
   */
  private Integer targetType;

  /**
   * LOGIN_PHONE / LOGIN_EMAIL / REGISTER_PHONE ...
   */
  private String bizType;

  /**
   * 验证码摘要
   */
  private String codeHash;

  /**
   * 验证码脱敏展示
   */
  private String codePreview;

  /**
   * 0-未使用，1-已使用，2-已过期，3-已作废
   */
  private Integer status;

  private LocalDateTime expireTime;

  private LocalDateTime usedTime;

  private LocalDateTime sendTime;

  /**
   * miniapp / h5 / app / admin
   */
  private String platform;

  private String templateCode;

  private String sceneKey;

  private String requestIp;

  private String deviceId;

  private Integer failCount;

  private Long messageLogId;

  private String remark;

  @TableField(fill = FieldFill.INSERT)
  private LocalDateTime createTime;

  @TableField(fill = FieldFill.INSERT_UPDATE)
  private LocalDateTime updateTime;

  private Integer isDeleted;
}