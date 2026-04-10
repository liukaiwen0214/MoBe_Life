package com.mobe.mobe_life_backend.auth.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "微信 code2Session 响应对象，用于接收微信登录换取会话结果")
@Data
public class WxCode2SessionVO {

  @Schema(description = "微信用户 openid", example = "oUpF8uMuAJO_M2pxb1Q9zNjWeS6o")
  private String openid;

  @Schema(description = "微信会话密钥", example = "tiihtNczf5v6AKRyjwEUhQ==")
  private String session_key;

  @Schema(description = "微信开放平台 unionid", example = "ocMvos6NjeKLIBqg5Mr9QjxrP1FA")
  private String unionid;

  @Schema(description = "微信错误码", example = "0")
  private Integer errcode;

  @Schema(description = "微信错误信息", example = "ok")
  private String errmsg;
}
