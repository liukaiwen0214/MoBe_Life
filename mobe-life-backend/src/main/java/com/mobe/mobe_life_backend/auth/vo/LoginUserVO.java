package com.mobe.mobe_life_backend.auth.vo;

import lombok.Data;

@Data
public class LoginUserVO {

  private Long userId;

  private String nickname;

  private String avatar;

  private String token;
}