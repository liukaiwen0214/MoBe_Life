/**
 * 核心职责：定义用户登录会话实体，用于服务端控制 JWT 是否仍然有效。
 * 所属业务模块：认证中心 / 登录会话。
 * 重要依赖关系或外部约束：字段需要与 `mobe_user_session` 表结构保持一致。
 */
package com.mobe.mobe_life_backend.auth.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;

@Schema(description = "用户登录会话实体，用于记录登录状态、设备信息和令牌生命周期")
@Data
@TableName("mobe_user_session")
public class MobeUserSession {

  @Schema(description = "主键ID", example = "1")
  @TableId(type = IdType.AUTO)
  private Long id;

  @Schema(description = "用户ID", example = "1")
  private Long userId;

  @Schema(description = "会话编号", example = "SESSION202605080001")
  private String sessionNo;

  @Schema(description = "访问令牌唯一ID，对应 JWT jti", example = "b8b5d9a7f0f44d80a8f2d4fbde30c0e6")
  private String accessTokenJti;

  @Schema(description = "刷新令牌唯一ID", example = "fb8a2b1bde664df3a5f26a5f6c29f5d2")
  private String refreshTokenJti;

  @Schema(description = "刷新令牌哈希值，不存明文")
  private String refreshTokenHash;

  @Schema(description = "登录方式：PASSWORD/EMAIL_CODE/WECHAT_MINI/WECHAT_WEB", example = "PASSWORD")
  private String loginType;

  @Schema(description = "平台来源：web/h5/miniapp/app", example = "web")
  private String platform;

  @Schema(description = "设备ID", example = "device-abc123")
  private String deviceId;

  @Schema(description = "设备名称", example = "Chrome on macOS")
  private String deviceName;

  @Schema(description = "设备类型：PC/MOBILE/TABLET/UNKNOWN", example = "PC")
  private String deviceType;

  @Schema(description = "浏览器名称", example = "Chrome")
  private String browser;

  @Schema(description = "操作系统", example = "macOS")
  private String os;

  @Schema(description = "登录IP", example = "127.0.0.1")
  private String loginIp;

  @Schema(description = "登录地点", example = "上海")
  private String loginLocation;

  @Schema(description = "User-Agent")
  private String userAgent;

  @Schema(description = "会话状态：ACTIVE/LOGOUT/EXPIRED/REVOKED", example = "ACTIVE")
  private String status;

  @Schema(description = "登录时间", example = "2026-05-08T10:00:00")
  private LocalDateTime loginTime;

  @Schema(description = "最后活跃时间", example = "2026-05-08T10:30:00")
  private LocalDateTime lastActiveTime;

  @Schema(description = "会话过期时间", example = "2026-05-15T10:00:00")
  private LocalDateTime expireTime;

  @Schema(description = "退出登录时间", example = "2026-05-08T11:00:00")
  private LocalDateTime logoutTime;

  @Schema(description = "强制失效时间", example = "2026-05-08T11:00:00")
  private LocalDateTime revokeTime;

  @Schema(description = "失效原因")
  private String revokeReason;

  @Schema(description = "备注说明")
  private String remark;

  @Schema(description = "创建时间", example = "2026-05-08T10:00:00")
  @TableField(fill = FieldFill.INSERT)
  private LocalDateTime createTime;

  @Schema(description = "更新时间", example = "2026-05-08T10:30:00")
  @TableField(fill = FieldFill.INSERT_UPDATE)
  private LocalDateTime updateTime;

  @Schema(description = "是否逻辑删除：0-否 1-是", example = "0")
  private Integer isDeleted;
}
