package com.mobe.mobe_life_backend.user.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("mobe_user")
public class MobeUser {

  @TableId(type = IdType.AUTO)
  private Long id;

  private String openid;

  private String unionid;

  private String phone;

  private String email;

  private String password;

  private String nickname;

  private String avatar;

  /**
   * 0-未知，1-男，2-女
   */
  private Integer gender;

  private LocalDate birthday;

  /**
   * 0-正常，1-禁用
   */
  private Integer status;

  @TableField(fill = FieldFill.INSERT)
  private LocalDateTime createTime;

  @TableField(fill = FieldFill.INSERT_UPDATE)
  private LocalDateTime updateTime;

  private Integer isDeleted;

  private String remark;
}