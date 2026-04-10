package com.mobe.mobe_life_backend.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Schema(description = "密码修改请求对象，用于已设置密码账号的改密场景")
@Data
public class ChangePasswordDTO {

  @Schema(description = "当前登录密码", requiredMode = Schema.RequiredMode.REQUIRED, example = "Old123456")
  @NotBlank(message = "原密码不能为空")
  private String oldPassword;

  @Schema(description = "新登录密码", requiredMode = Schema.RequiredMode.REQUIRED, example = "New123456")
  @NotBlank(message = "新密码不能为空")
  private String newPassword;

  @Schema(description = "确认新密码", requiredMode = Schema.RequiredMode.REQUIRED, example = "New123456")
  @NotBlank(message = "确认密码不能为空")
  private String confirmPassword;
}
