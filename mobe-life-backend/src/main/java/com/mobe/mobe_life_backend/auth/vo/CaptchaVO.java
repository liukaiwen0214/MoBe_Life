package com.mobe.mobe_life_backend.auth.vo;

import lombok.Data;

@Data
public class CaptchaVO {

  private String captchaKey;

  private String captchaImage;
}