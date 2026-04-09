/**
 * 核心职责：映射用户主表，承载微信身份、本地账号资料和基础状态信息。
 * 所属业务模块：用户中心 / 领域实体。
 * 重要依赖关系或外部约束：字段定义需与 `mobe_user` 表保持一致；认证和用户模块都以该实体作为核心用户模型。
 */
package com.mobe.mobe_life_backend.user.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户实体。
 *
 * <p>设计初衷是把“微信登录身份”“联系方式”“本地密码能力”“用户资料”汇聚在同一个核心实体中，
 * 便于认证链路和个人中心围绕同一用户主记录协作。</p>
 *
 * <p>线程安全性：实体对象可变，不应跨线程共享；并发一致性依赖数据库更新和上层事务控制。</p>
 */
@Data
@TableName("mobe_user")
public class MobeUser {

  /** 用户主键，自增。 */
  @TableId(type = IdType.AUTO)
  private Long id;

  /** 微信 openid，是当前小程序内识别用户的核心锚点。 */
  private String openid;

  /** 微信 unionid，可能为空；主要用于未来跨应用统一用户身份。 */
  private String unionid;

  /** 绑定手机号，允许为空。 */
  private String phone;

  /** 绑定邮箱，允许为空。 */
  private String email;

  /** BCrypt 密文密码；微信一键登录用户初始可为空。 */
  private String password;

  /** 昵称，允许为空；首次微信登录时通常会给默认值。 */
  private String nickname;

  /** 头像地址，允许为空。 */
  private String avatar;

  /** 性别编码，当前约定 `0` 未知、`1` 男、`2` 女。 */
  private Integer gender;

  /** 出生日期，允许为空。 */
  private LocalDate birthday;

  /** 账号状态，当前主要用 `0` 正常、`1` 禁用。 */
  private Integer status;

  /** 创建时间，由框架自动填充。 */
  @TableField(fill = FieldFill.INSERT)
  private LocalDateTime createTime;

  /** 更新时间，由框架自动填充。 */
  @TableField(fill = FieldFill.INSERT_UPDATE)
  private LocalDateTime updateTime;

  /** 逻辑删除标记，`0` 未删除、`1` 已删除。 */
  private Integer isDeleted;

  /** 备注，允许为空；主要预留给运营或后续扩展。 */
  private String remark;
}
