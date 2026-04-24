/**
 * 核心职责：封装认证中心的请求参数模型，承载控制层到服务层的输入数据。
 * 所属业务模块：认证中心 / DTO。
 * 重要依赖关系或外部约束：通常与参数校验注解配合使用，字段命名需要与接口语义保持一致。
 */
package com.mobe.mobe_life_backend.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Schema(description = "邮箱验证码发送请求对象，用于发送邮箱验证码场景")
@Data
public class SendEmailCodeDTO {

  @Schema(description = "目标邮箱地址", requiredMode = Schema.RequiredMode.REQUIRED, example = "user@example.com")
  @NotBlank(message = "邮箱不能为空")
  @Email(message = "邮箱格式不正确")
  private String email;
}
