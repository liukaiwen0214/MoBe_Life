package com.mobe.mobe_life_backend.auth.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "登录结果对象，用于返回登录用户基础信息与访问令牌")
@Data
public class LoginUserVO {

  @Schema(description = "用户ID", example = "1")
  private Long userId;

  @Schema(description = "用户昵称", example = "MoBe用户")
  private String nickname;

  @Schema(description = "用户头像地址", example = "https://cdn.mobe.com/avatar/default.png")
  private String avatar;

  @Schema(description = "访问令牌", example = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjF9.signature")
  private String token;
}
