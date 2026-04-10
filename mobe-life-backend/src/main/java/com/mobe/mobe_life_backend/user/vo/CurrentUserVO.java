package com.mobe.mobe_life_backend.user.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "当前用户信息响应对象，用于返回个人中心展示所需资料与账号状态")
@Data
public class CurrentUserVO {

  @Schema(description = "用户ID", example = "1")
  private Long id;

  @Schema(description = "微信 openid", example = "oUpF8uMuAJO_M2pxb1Q9zNjWeS6o")
  private String openid;

  @Schema(description = "绑定手机号", example = "13812345678")
  private String phone;

  @Schema(description = "绑定邮箱", example = "user@example.com")
  private String email;

  @Schema(description = "用户昵称", example = "MoBe用户")
  private String nickname;

  @Schema(description = "头像地址", example = "https://cdn.mobe.com/avatar/default.png")
  private String avatar;

  @Schema(description = "性别编码，0-未知，1-男，2-女", example = "1")
  private Integer gender;

  @Schema(description = "账号状态，0-正常，1-禁用", example = "0")
  private Integer status;

  @Schema(description = "出生日期", example = "1998-08-18")
  private String birthday;

  @Schema(description = "是否已设置密码", example = "true")
  private Boolean hasPassword;

  @Schema(description = "是否已绑定手机号", example = "true")
  private Boolean hasPhone;

  @Schema(description = "是否已绑定邮箱", example = "true")
  private Boolean hasEmail;
}
