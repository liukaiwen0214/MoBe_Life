package com.mobe.mobe_life_backend.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class WxMiniLoginDTO {

  @NotBlank(message = "code不能为空")
  private String code;
}