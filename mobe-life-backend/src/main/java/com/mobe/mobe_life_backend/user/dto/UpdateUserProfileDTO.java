package com.mobe.mobe_life_backend.user.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateUserProfileDTO {

  private String nickname;

  private String avatar;

  private Integer gender;

  private LocalDate birthday;
}