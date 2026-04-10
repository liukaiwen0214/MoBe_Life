package com.mobe.mobe_life_backend.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Schema(description = "邮箱解绑请求对象，用于当前登录用户解绑邮箱场景")
@Data
public class UnbindEmailDTO {

  @Schema(description = "邮箱解绑验证码", requiredMode = Schema.RequiredMode.REQUIRED, example = "123456")
  @NotBlank(message = "验证码不能为空")
  private String code;
}
