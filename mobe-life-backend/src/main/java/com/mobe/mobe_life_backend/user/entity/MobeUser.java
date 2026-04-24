/**
 * 核心职责：定义用户中心的数据实体，用于映射数据库记录或领域状态。
 * 所属业务模块：用户中心 / 实体模型。
 * 重要依赖关系或外部约束：字段通常需要与数据库表结构、MyBatis-Plus 映射约定保持一致。
 */
package com.mobe.mobe_life_backend.user.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;

@Schema(description = "用户实体对象，用于映射系统用户基础信息")
@Data
@TableName("mobe_user")
public class MobeUser {

  @Schema(description = "用户ID", example = "1")
  @TableId(type = IdType.AUTO)
  private Long id;

  @Schema(description = "微信 openid", example = "oUpF8uMuAJO_M2pxb1Q9zNjWeS6o")
  private String openid;

  @Schema(description = "微信 unionid", example = "ocMvos6NjeKLIBqg5Mr9QjxrP1FA")
  private String unionid;

  @Schema(description = "绑定手机号", example = "13812345678")
  private String phone;

  @Schema(description = "绑定邮箱", example = "user@example.com")
  private String email;

  @Schema(description = "登录密码密文", example = "$2a$10$abcdefghijklmnopqrstuv")
  private String password;

  @Schema(description = "用户昵称", example = "MoBe用户")
  private String nickname;

  @Schema(description = "头像地址", example = "https://cdn.mobe.com/avatar/default.png")
  private String avatar;

  @Schema(description = "性别编码，0-未知，1-男，2-女", example = "1")
  private Integer gender;

  @Schema(description = "出生日期", example = "1998-08-18")
  private LocalDate birthday;

  @Schema(description = "账号状态，0-正常，1-禁用", example = "0")
  private Integer status;

  @Schema(description = "创建时间", example = "2026-04-10T09:00:00")
  @TableField(fill = FieldFill.INSERT)
  private LocalDateTime createTime;

  @Schema(description = "更新时间", example = "2026-04-10T10:00:00")
  @TableField(fill = FieldFill.INSERT_UPDATE)
  private LocalDateTime updateTime;

  @Schema(description = "逻辑删除标记，0-未删除，1-已删除", example = "0")
  private Integer isDeleted;

  @Schema(description = "备注说明", example = "微信注册用户")
  private String remark;
}
