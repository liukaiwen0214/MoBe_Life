package com.mobe.mobe_life_backend.auth.vo;

import lombok.Data;

@Data
public class WxPhoneNumberVO {

  private String phoneNumber;
  private String purePhoneNumber;
  private String countryCode;

  private Integer errcode;
  private String errmsg;
}