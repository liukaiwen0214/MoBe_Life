/**
 * 核心职责：通过腾讯云 SES 发送认证模块所需的邮箱验证码邮件。
 * 所属业务模块：认证中心 / 邮件通道实现。
 * 重要依赖关系或外部约束：依赖腾讯云 SES Java SDK、邮件模板配置以及已验证的发信地址；
 * 一旦模板变量结构或区域配置不一致，会直接影响验证码送达。
 */
package com.mobe.mobe_life_backend.auth.service.impl;

import com.mobe.mobe_life_backend.auth.config.TencentSesProperties;
import com.mobe.mobe_life_backend.auth.service.EmailService;
import com.mobe.mobe_life_backend.auth.vo.EmailSendResult;
import com.mobe.mobe_life_backend.common.exception.BusinessException;
import com.mobe.mobe_life_backend.common.exception.CommonErrorCode;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.ses.v20201002.SesClient;
import com.tencentcloudapi.ses.v20201002.models.SendEmailRequest;
import com.tencentcloudapi.ses.v20201002.models.SendEmailResponse;
import com.tencentcloudapi.ses.v20201002.models.Template;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 腾讯云 SES 邮件服务实现。
 *
 * <p>设计初衷是在认证模块内部提供一个稳定的“发送绑定邮箱验证码”能力，
 * 让上层服务只关心业务目标邮箱和验证码，不关心 SDK 初始化、模板拼装和错误转换。</p>
 *
 * <p>线程安全性：本类本身不保存请求级状态，Spring 单例使用是安全的；
 * 但每次调用都会新建 SES Client，避免把 SDK 客户端的潜在线程模型假设泄漏到业务层。</p>
 */
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

  /**
   * SES 配置。
   * 通过集中配置避免把密钥和模板 ID 写死在业务代码里。
   */
  private final TencentSesProperties tencentSesProperties;

  /**
   * 发送绑定邮箱验证码邮件。
   *
   * @param toEmail 收件邮箱，不允许为 null 或空白；调用前应已完成格式校验和归一化。
   * @param code 验证明文，不允许为 null 或空白；仅用于本次模板渲染，不落日志。
   * @return 发送结果，不返回 null；成功时包含腾讯云请求 ID，供消息日志追踪。
   * @throws BusinessException 当腾讯云凭证错误、模板错误、网络失败或供应商拒绝发送时抛出。
   * @implNote 该方法会发起远程调用，并依赖第三方邮件通道可用性；失败时不会自动重试。
   */
  @Override
  public EmailSendResult sendBindEmailCode(String toEmail, String code) {
    try {
      Credential credential = new Credential(
          tencentSesProperties.getSecretId(),
          tencentSesProperties.getSecretKey());

      HttpProfile httpProfile = new HttpProfile();
      httpProfile.setEndpoint("ses.tencentcloudapi.com");

      ClientProfile clientProfile = new ClientProfile();
      clientProfile.setHttpProfile(httpProfile);

      SesClient client = new SesClient(
          credential,
          tencentSesProperties.getRegion(),
          clientProfile);

      SendEmailRequest request = new SendEmailRequest();
      request.setFromEmailAddress(
          tencentSesProperties.getFromName() + " <" + tencentSesProperties.getFromEmail() + ">");
      request.setDestination(new String[] {toEmail});
      request.setSubject("MoBe Life 邮箱验证码");

      Template template = new Template();
      template.setTemplateID(tencentSesProperties.getTemplateId());
      // 模板变量必须和腾讯云控制台模板占位符一致，否则看似调用成功也可能发出错误内容。
      template.setTemplateData("{\"code\":\"" + code + "\"}");
      request.setTemplate(template);

      SendEmailResponse response = client.SendEmail(request);

      EmailSendResult result = new EmailSendResult();
      result.setProviderMessageId(response.getRequestId());
      result.setResponseContent("{\"requestId\":\"" + response.getRequestId() + "\"}");
      return result;
    } catch (Exception e) {
      // 统一转换成业务异常，避免控制层暴露第三方 SDK 异常类型并把供应商实现细节泄漏到上层。
      throw new BusinessException(CommonErrorCode.SERVICE_UNAVAILABLE, "发送邮件失败：" + e.getMessage());
    }
  }
}
