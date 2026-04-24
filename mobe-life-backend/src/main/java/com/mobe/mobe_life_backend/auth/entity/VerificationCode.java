/**
 * 核心职责：定义认证中心的数据实体，用于映射数据库记录或领域状态。
 * 所属业务模块：认证中心 / 实体模型。
 * 重要依赖关系或外部约束：字段通常需要与数据库表结构、MyBatis-Plus 映射约定保持一致。
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

@Schema(description = "验证码实体，用于记录验证码生成、发送和校验生命周期数据")
@Data
@TableName("verification_code")
public class VerificationCode {

  @Schema(description = "验证码记录ID", example = "2001")
  @TableId(type = IdType.AUTO)
  private Long id;

  @Schema(description = "验证码接收目标", example = "user@example.com")
  private String target;

  @Schema(description = "目标类型，1-手机号，2-邮箱", example = "2")
  private Integer targetType;

  @Schema(description = "业务场景编码", example = "BIND_EMAIL")
  private String bizType;

  @Schema(description = "验证码哈希值", example = "8d969eef6ecad3c29a3a629280e686cf")
  private String codeHash;

  @Schema(description = "验证码脱敏预览", example = "**3456")
  private String codePreview;

  @Schema(description = "验证码状态，0-未使用，1-已使用，2-已过期，3-已作废", example = "0")
  private Integer status;

  @Schema(description = "过期时间", example = "2026-04-10T10:05:00")
  private LocalDateTime expireTime;

  @Schema(description = "使用时间", example = "2026-04-10T10:02:00")
  private LocalDateTime usedTime;

  @Schema(description = "发送时间", example = "2026-04-10T10:00:00")
  private LocalDateTime sendTime;

  @Schema(description = "发起平台标识", example = "miniapp")
  private String platform;

  @Schema(description = "消息模板编码", example = "EMAIL_BIND_TEMPLATE")
  private String templateCode;

  @Schema(description = "业务细分场景键", example = "bind_email_confirm")
  private String sceneKey;

  @Schema(description = "请求来源IP", example = "127.0.0.1")
  private String requestIp;

  @Schema(description = "设备标识", example = "device-abc123")
  private String deviceId;

  @Schema(description = "校验失败次数", example = "0")
  private Integer failCount;

  @Schema(description = "关联消息日志ID", example = "1001")
  private Long messageLogId;

  @Schema(description = "备注说明", example = "绑定邮箱验证码")
  private String remark;

  @Schema(description = "创建时间", example = "2026-04-10T10:00:00")
  @TableField(fill = FieldFill.INSERT)
  private LocalDateTime createTime;

  @Schema(description = "更新时间", example = "2026-04-10T10:01:00")
  @TableField(fill = FieldFill.INSERT_UPDATE)
  private LocalDateTime updateTime;

  @Schema(description = "逻辑删除标记，0-未删除，1-已删除", example = "0")
  private Integer isDeleted;
}
