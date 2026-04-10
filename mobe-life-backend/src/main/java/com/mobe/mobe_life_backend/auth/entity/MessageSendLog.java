package com.mobe.mobe_life_backend.auth.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;

@Schema(description = "消息发送日志实体，用于记录验证码或通知消息的发送审计信息")
@Data
@TableName("message_send_log")
public class MessageSendLog {

  @Schema(description = "日志主键ID", example = "1001")
  @TableId(type = IdType.AUTO)
  private Long id;

  @Schema(description = "发送渠道，1-短信，2-邮件", example = "2")
  private Integer channel;

  @Schema(description = "业务类型编码", example = "BIND_EMAIL")
  private String bizType;

  @Schema(description = "发送目标账号", example = "user@example.com")
  private String target;

  @Schema(description = "消息模板编码", example = "EMAIL_BIND_TEMPLATE")
  private String templateCode;

  @Schema(description = "消息服务提供商", example = "TENCENT_SES")
  private String provider;

  @Schema(description = "服务商消息ID", example = "ses-msg-202604100001")
  private String providerMessageId;

  @Schema(description = "发送请求内容摘要", example = "{\"email\":\"user@example.com\"}")
  private String requestContent;

  @Schema(description = "发送响应内容摘要", example = "{\"status\":\"SUCCESS\"}")
  private String responseContent;

  @Schema(description = "发送状态，0-待发送，1-成功，2-失败", example = "1")
  private Integer sendStatus;

  @Schema(description = "发送失败原因", example = "邮箱地址不存在")
  private String failReason;

  @Schema(description = "重试次数", example = "0")
  private Integer retryCount;

  @Schema(description = "请求来源IP", example = "127.0.0.1")
  private String requestIp;

  @Schema(description = "发起平台标识", example = "miniapp")
  private String platform;

  @Schema(description = "实际发送时间", example = "2026-04-10T10:00:00")
  private LocalDateTime sendTime;

  @Schema(description = "备注说明", example = "绑定邮箱验证码发送成功")
  private String remark;

  @Schema(description = "创建时间", example = "2026-04-10T09:59:59")
  @TableField(fill = FieldFill.INSERT)
  private LocalDateTime createTime;

  @Schema(description = "更新时间", example = "2026-04-10T10:00:01")
  @TableField(fill = FieldFill.INSERT_UPDATE)
  private LocalDateTime updateTime;

  @Schema(description = "逻辑删除标记，0-未删除，1-已删除", example = "0")
  private Integer isDeleted;
}
