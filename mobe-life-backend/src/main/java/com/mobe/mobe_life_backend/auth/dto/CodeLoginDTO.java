package com.mobe.mobe_life_backend.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Schema(description = "验证码登录请求对象，用于邮箱验证码快捷登录场景")
@Data
public class CodeLoginDTO {

  @Schema(description = "登录账号，可传邮箱或手机号", requiredMode = Schema.RequiredMode.REQUIRED, example = "user@example.com")
  @NotBlank(message = "账号不能为空")
  private String account;

  @Schema(description = "登录验证码", requiredMode = Schema.RequiredMode.REQUIRED, example = "123456")
  @NotBlank(message = "验证码不能为空")
  private String code;
}
