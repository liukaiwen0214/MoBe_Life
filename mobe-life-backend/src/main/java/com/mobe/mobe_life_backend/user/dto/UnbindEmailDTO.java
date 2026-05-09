/**
 * 封装用户中心的请求入参，承接控制层到服务层的字段传递。
 * 模块：用户中心 / DTO。
 * 约束：通常与参数校验注解配合使用，字段命名需要与接口语义保持一致。
 */
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
