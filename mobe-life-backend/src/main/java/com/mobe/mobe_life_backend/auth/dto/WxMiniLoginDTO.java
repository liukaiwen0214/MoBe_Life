package com.mobe.mobe_life_backend.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Schema(description = "微信小程序登录请求对象，用于微信授权登录场景")
@Data
public class WxMiniLoginDTO {

  @Schema(description = "微信小程序登录 code", requiredMode = Schema.RequiredMode.REQUIRED, example = "0a1b2c3d4e5f6g7h")
  @NotBlank(message = "code不能为空")
  private String code;
}
