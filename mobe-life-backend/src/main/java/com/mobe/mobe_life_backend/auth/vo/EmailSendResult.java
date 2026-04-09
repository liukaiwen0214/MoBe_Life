/**
 * 核心职责：承载邮件发送结果的最小必要信息，供消息日志回写使用。
 * 所属业务模块：认证中心 / 邮件通道返回值。
 * 重要依赖关系或外部约束：字段内容由具体邮件服务实现填充，调用方不应假设一定存在完整响应体。
 */
package com.mobe.mobe_life_backend.auth.vo;

import lombok.Data;

/**
 * 邮件发送结果。
 *
 * <p>设计初衷是把第三方 SDK 的复杂响应裁剪成认证业务真正需要的两类信息：
 * 可追踪的供应商消息标识，以及可落库的响应摘要。</p>
 */
@Data
public class EmailSendResult {

  /**
   * 供应商侧消息标识。
   * 可能为空，取决于第三方平台是否返回稳定请求号；若存在，消息日志会用它做追踪主键。
   */
  private String providerMessageId;

  /**
   * 响应内容摘要。
   * 允许为空；用于把第三方结果以可审计形式回写到消息日志，而不直接暴露 SDK 对象。
   */
  private String responseContent;
}
