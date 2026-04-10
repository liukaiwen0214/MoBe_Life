package com.mobe.mobe_life_backend.auth.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "微信手机号响应对象，用于承接微信手机号授权解析结果")
@Data
public class WxPhoneNumberVO {

  @Schema(description = "完整手机号", example = "+8613812345678")
  private String phoneNumber;

  @Schema(description = "纯手机号", example = "13812345678")
  private String purePhoneNumber;

  @Schema(description = "国家区号", example = "86")
  private String countryCode;

  @Schema(description = "微信错误码", example = "0")
  private Integer errcode;

  @Schema(description = "微信错误信息", example = "ok")
  private String errmsg;
}
