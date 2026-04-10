package com.mobe.mobe_life_backend.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Schema(description = "账号密码登录请求对象，用于账号密码登录场景")
@Data
public class PasswordLoginDTO {

  @Schema(description = "登录账号，可传邮箱或手机号", requiredMode = Schema.RequiredMode.REQUIRED, example = "user@example.com")
  @NotBlank(message = "账号不能为空")
  private String account;

  @Schema(description = "登录密码", requiredMode = Schema.RequiredMode.REQUIRED, example = "Abc123456")
  @NotBlank(message = "密码不能为空")
  private String password;

  @Schema(description = "图形验证码标识", requiredMode = Schema.RequiredMode.REQUIRED, example = "captcha_123456")
  @NotBlank(message = "验证码标识不能为空")
  private String captchaKey;

  @Schema(description = "图形验证码内容", requiredMode = Schema.RequiredMode.REQUIRED, example = "8K3P")
  @NotBlank(message = "验证码不能为空")
  private String captchaCode;
}
