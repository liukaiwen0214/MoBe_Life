package com.mobe.mobe_life_backend.user.vo;

import lombok.Data;

@Data
public class CurrentUserVO {

  private Long id;

  private String openid;

  private String phone;

  private String email;

  private String nickname;

  private String avatar;

  private Integer gender;

  private Integer status;

  private String birthday;

  private Boolean hasPassword;

  private Boolean hasPhone;

  private Boolean hasEmail;
}