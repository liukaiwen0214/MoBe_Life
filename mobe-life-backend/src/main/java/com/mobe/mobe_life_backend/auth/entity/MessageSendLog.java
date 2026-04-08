package com.mobe.mobe_life_backend.auth.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("message_send_log")
public class MessageSendLog {

  @TableId(type = IdType.AUTO)
  private Long id;

  /**
   * 1-短信，2-邮件
   */
  private Integer channel;

  private String bizType;

  /**
   * 手机号或邮箱
   */
  private String target;

  private String templateCode;

  /**
   * 阿里云短信 / 腾讯云 / SendCloud ...
   */
  private String provider;

  private String providerMessageId;

  private String requestContent;

  private String responseContent;

  /**
   * 0-待发送，1-成功，2-失败
   */
  private Integer sendStatus;

  private String failReason;

  private Integer retryCount;

  private String requestIp;

  /**
   * miniapp / h5 / app / admin
   */
  private String platform;

  private LocalDateTime sendTime;

  private String remark;

  @TableField(fill = FieldFill.INSERT)
  private LocalDateTime createTime;

  @TableField(fill = FieldFill.INSERT_UPDATE)
  private LocalDateTime updateTime;

  private Integer isDeleted;
}