/**
 * 核心职责：封装认证中心返回给前端或上层调用方的展示模型。
 * 所属业务模块：认证中心 / VO。
 * 重要依赖关系或外部约束：字段设计更偏向展示与接口契约，不一定与数据库结构一一对应。
 */
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
