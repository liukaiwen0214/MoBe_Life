/**
 * 核心职责：承接腾讯云 SES 邮件发送配置，避免认证模块直接依赖硬编码凭证和模板编号。
 * 所属业务模块：认证中心 / 基础设施配置。
 * 重要依赖关系或外部约束：配置项需与 `application*.yml` 中的 `tencent.ses` 前缀保持一致，
 * 且依赖腾讯云 SES 已完成发信域名、模板和区域的控制台配置。
 */
package com.mobe.mobe_life_backend.auth.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 腾讯云 SES 配置载体。
 *
 * <p>设计初衷是在 Spring 容器启动阶段完成配置绑定，让邮件发送服务只关心“发什么业务邮件”，
 * 不关心“凭证和模板从哪里来”，从而降低实现类对部署环境的耦合。</p>
 *
 * <p>线程安全性：本类作为单例配置 Bean 使用，运行期只读；调用方不应在业务代码中修改其字段值。</p>
 *
 * <p>使用示例：</p>
 *
 * <pre>{@code
 * String region = tencentSesProperties.getRegion();
 * Long templateId = tencentSesProperties.getTemplateId();
 * }</pre>
 */
@Data
@Component
@ConfigurationProperties(prefix = "tencent.ses")
public class TencentSesProperties {

  /**
   * 腾讯云 API 密钥 ID。
   * 仅用于服务端鉴权，不能为空；泄漏后会直接影响邮件通道安全。
   */
  private String secretId;

  /**
   * 腾讯云 API 密钥 Key。
   * 与 `secretId` 成对使用，必须通过安全配置中心或环境变量注入，不能写入前端或日志。
   */
  private String secretKey;

  /**
   * SES 所在地域。
   * 需要与模板、发信地址实际部署区域一致，否则会出现调用成功但业务模板不可用的隐性故障。
   */
  private String region;

  /**
   * 发信邮箱地址。
   * 必须是 SES 已验证的地址或域名下地址，否则绑定邮箱验证码链路会在远程调用阶段失败。
   */
  private String fromEmail;

  /**
   * 发件人展示名称。
   * 业务上用于提升邮件可信度，避免用户把验证码邮件误判为垃圾邮件。
   */
  private String fromName;

  /**
   * 腾讯云 SES 模板编号。
   * 当前认证模块默认使用该模板渲染“绑定邮箱验证码”邮件，因此模板变量结构必须与实现中的 JSON 保持一致。
   */
  private Long templateId;
}
