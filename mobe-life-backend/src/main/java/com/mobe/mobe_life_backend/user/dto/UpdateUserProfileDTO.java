package com.mobe.mobe_life_backend.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.Data;

@Schema(description = "用户资料更新请求对象，用于当前登录用户更新个人资料场景")
@Data
public class UpdateUserProfileDTO {

  @Schema(description = "用户昵称", example = "MoBe用户")
  private String nickname;

  @Schema(description = "用户头像地址", example = "https://cdn.mobe.com/avatar/default.png")
  private String avatar;

  @Schema(description = "性别编码，0-未知，1-男，2-女", example = "1")
  private Integer gender;

  @Schema(description = "出生日期", example = "1998-08-18")
  private LocalDate birthday;
}
