package com.mobe.mobe_life_backend.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UnbindEmailDTO {

  @NotBlank(message = "验证码不能为空")
  private String code;
}