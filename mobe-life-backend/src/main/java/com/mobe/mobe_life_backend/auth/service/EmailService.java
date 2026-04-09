/**
 * 核心职责：抽象认证相关邮件发送能力，隔离业务逻辑与具体邮件供应商 SDK。
 * 所属业务模块：认证中心 / 基础设施服务抽象。
 * 重要依赖关系或外部约束：实现类需要对接第三方邮件通道，并返回可用于审计的发送结果。
 */
package com.mobe.mobe_life_backend.auth.service;

import com.mobe.mobe_life_backend.auth.vo.EmailSendResult;

/**
 * 邮件服务接口。
 *
 * <p>设计初衷是把“发送验证码邮件”的业务语义与“腾讯云 SES SDK 调用细节”隔离开，
 * 这样认证服务只依赖稳定接口，未来更换供应商时不需要重写上层业务流程。</p>
 *
 * <p>线程安全性：实现类通常以 Spring 单例服务存在，必须避免保存请求级可变状态。</p>
 */
public interface EmailService {

  /**
   * 发送绑定邮箱验证码邮件。
   *
   * @param toEmail 收件邮箱，不允许为 null 或空白；调用方应保证已做格式校验并完成统一小写处理。
   * @param code 验证明文，不允许为 null 或空白；只在本次发送请求中短暂使用，不应写入日志。
   * @return 邮件发送结果，不返回 null；至少包含供应商侧消息标识或响应摘要，便于消息日志回写。
   * @throws RuntimeException 当第三方邮件服务不可达、配置错误或业务拒绝发信时抛出。
   * @implNote 该方法会发起远程网络调用，且存在外部系统超时、限流和模板配置错误等副作用。
   */
  EmailSendResult sendBindEmailCode(String toEmail, String code);
}
