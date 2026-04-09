package com.mobe.mobe_life_backend.auth.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "tencent.ses")
public class TencentSesProperties {

  private String secretId;

  private String secretKey;

  private String region;

  private String fromEmail;

  private String fromName;

  private Long templateId;
}