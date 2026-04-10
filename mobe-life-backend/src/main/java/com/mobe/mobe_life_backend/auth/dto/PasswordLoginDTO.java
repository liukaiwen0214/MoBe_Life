package com.mobe.mobe_life_backend.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PasswordLoginDTO {

  @NotBlank(message = "账号不能为空")
  private String account;

  @NotBlank(message = "密码不能为空")
  private String password;

  @NotBlank(message = "验证码标识不能为空")
  private String captchaKey;

  @NotBlank(message = "验证码不能为空")
  private String captchaCode;
}