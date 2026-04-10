package com.mobe.mobe_life_backend.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Schema(description = "手机号绑定请求对象，用于微信手机号授权绑定场景")
@Data
public class BindPhoneDTO {

  @Schema(description = "微信手机号授权 code", requiredMode = Schema.RequiredMode.REQUIRED, example = "1234567890abcdef")
  @NotBlank(message = "手机号code不能为空")
  private String code;
}
