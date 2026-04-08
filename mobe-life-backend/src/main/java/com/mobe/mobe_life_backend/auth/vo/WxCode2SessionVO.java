package com.mobe.mobe_life_backend.auth.vo;

import lombok.Data;

@Data
public class WxCode2SessionVO {

  private String openid;

  private String session_key;

  private String unionid;

  private Integer errcode;

  private String errmsg;
}