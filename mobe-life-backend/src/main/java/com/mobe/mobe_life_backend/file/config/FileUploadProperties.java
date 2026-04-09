/**
 * 核心职责：承接本地文件上传配置。
 * 所属业务模块：文件服务 / 基础配置。
 * 重要依赖关系或外部约束：配置项需与 `application*.yml` 中的 `file.upload` 前缀保持一致，且目录对运行进程可写。
 */
package com.mobe.mobe_life_backend.file.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 文件上传配置。
 *
 * <p>设计初衷是把存储路径和访问前缀从代码中抽离，便于开发、测试和生产环境使用不同磁盘目录或 CDN 前缀。</p>
 */
@Data
@Component
@ConfigurationProperties(prefix = "file.upload")
public class FileUploadProperties {

  /** 本地上传根目录；必须存在写权限。 */
  private String path;

  /** 文件访问前缀；前端最终通过它拼出可访问 URL。 */
  private String accessUrlPrefix;
}
