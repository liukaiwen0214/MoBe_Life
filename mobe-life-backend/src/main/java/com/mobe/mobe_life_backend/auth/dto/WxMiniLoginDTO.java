/**
 * 封装认证中心的请求入参，承接控制层到服务层的字段传递。
 * 模块：认证中心 / DTO。
 * 约束：通常与参数校验注解配合使用，字段命名需要与接口语义保持一致。
 */
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
