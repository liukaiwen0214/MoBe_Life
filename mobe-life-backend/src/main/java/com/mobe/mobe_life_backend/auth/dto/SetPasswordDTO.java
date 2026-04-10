package com.mobe.mobe_life_backend.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Schema(description = "初始密码设置请求对象，用于首次设置登录密码场景")
@Data
public class SetPasswordDTO {

  @Schema(description = "新登录密码", requiredMode = Schema.RequiredMode.REQUIRED, example = "Abc123456")
  @NotBlank(message = "新密码不能为空")
  private String newPassword;

  @Schema(description = "确认新密码", requiredMode = Schema.RequiredMode.REQUIRED, example = "Abc123456")
  @NotBlank(message = "确认密码不能为空")
  private String confirmPassword;
}
