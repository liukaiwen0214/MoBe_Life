/**
 * 核心职责：封装认证中心的请求参数模型，承载控制层到服务层的输入数据。
 * 所属业务模块：认证中心 / DTO。
 * 重要依赖关系或外部约束：通常与参数校验注解配合使用，字段命名需要与接口语义保持一致。
 */
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
