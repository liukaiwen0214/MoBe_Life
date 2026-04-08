package com.mobe.mobe_life_backend.file.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "file.upload")
public class FileUploadProperties {

  /**
   * 本地上传根目录
   */
  private String path;

  /**
   * 访问前缀
   */
  private String accessUrlPrefix;
}