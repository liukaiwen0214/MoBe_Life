package com.mobe.mobe_life_backend.auth.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "邮件发送结果对象，用于封装第三方邮件服务发送回执")
@Data
public class EmailSendResult {

  @Schema(description = "邮件服务商消息标识", example = "ses-msg-202604100001")
  private String providerMessageId;

  @Schema(description = "邮件服务响应内容", example = "{\"requestId\":\"req-123456\",\"status\":\"SUCCESS\"}")
  private String responseContent;
}
