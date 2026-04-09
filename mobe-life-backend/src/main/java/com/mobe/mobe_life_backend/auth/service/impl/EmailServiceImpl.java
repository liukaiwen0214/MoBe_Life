package com.mobe.mobe_life_backend.auth.service.impl;

import com.mobe.mobe_life_backend.auth.config.TencentSesProperties;
import com.mobe.mobe_life_backend.auth.service.EmailService;
import com.mobe.mobe_life_backend.common.exception.BusinessException;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.ses.v20201002.SesClient;
import com.tencentcloudapi.ses.v20201002.models.SendEmailRequest;
import com.tencentcloudapi.ses.v20201002.models.Template;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

  private final TencentSesProperties tencentSesProperties;

  @Override
  public void sendBindEmailCode(String toEmail, String code) {
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
      request.setDestination(new String[] { toEmail });
      request.setSubject("MoBe Life 邮箱验证码");
      Template template = new Template();
      template.setTemplateID(tencentSesProperties.getTemplateId());
      template.setTemplateData("{\"code\":\"" + code + "\"}");
      request.setTemplate(template);

      client.SendEmail(request);
    } catch (Exception e) {
      throw new BusinessException("发送邮件失败：" + e.getMessage());
    }
  }
}