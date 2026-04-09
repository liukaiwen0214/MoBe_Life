/**
 * 核心职责：记录验证码或通知消息的发送请求与结果，支撑审计、排障和重试策略。
 * 所属业务模块：认证中心 / 消息发送审计。
 * 重要依赖关系或外部约束：字段结构需与 `message_send_log` 表一致，且发送状态含义要和业务代码保持一致。
 */
package com.mobe.mobe_life_backend.auth.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 消息发送日志实体。
 *
 * <p>设计初衷是把“验证码记录”和“外部通道调用结果”分开建模：验证码表负责校验闭环，
 * 日志表负责审计和通道追踪。这样即便发送失败，也能保留失败原因、请求来源和供应商回执。</p>
 *
 * <p>线程安全性：实体本身不是线程安全对象，只应在单次数据库交互或事务上下文中使用。</p>
 */
@Data
@TableName("message_send_log")
public class MessageSendLog {

  /**
   * 主键。
   * 使用数据库自增，保证同一发送链路在日志系统中有稳定标识。
   */
  @TableId(type = IdType.AUTO)
  private Long id;

  /**
   * 发送渠道。
   * 当前约定 `1` 表示短信、`2` 表示邮件；保留整数而不是枚举字段，便于和历史表结构兼容。
   */
  private Integer channel;

  /**
   * 业务类型。
   * 用于区分“绑定邮箱”“找回密码”等不同发送场景，决定模板和校验链路。
   */
  private String bizType;

  /**
   * 发送目标。
   * 存储手机号或邮箱原值，便于按目标排查频繁发送或投诉问题。
   */
  private String target;

  /**
   * 通道模板编号。
   * 和第三方平台上的模板保持映射，方便后期核查到底用了哪个模板发信。
   */
  private String templateCode;

  /**
   * 通道提供方标识。
   * 例如腾讯云 SES；显式保留供应商信息，便于未来切换多通道或灰度路由。
   */
  private String provider;

  /**
   * 第三方平台返回的消息标识。
   * 可能为空，取决于供应商是否返回稳定消息 ID。
   */
  private String providerMessageId;

  /**
   * 发送请求摘要。
   * 当前实现只记录业务摘要而不落敏感正文，避免验证码明文进入数据库。
   */
  private String requestContent;

  /**
   * 供应商返回内容摘要。
   * 主要用于排障；如果包含敏感信息，调用方应做脱敏处理后再写入。
   */
  private String responseContent;

  /**
   * 发送状态。
   * 当前约定 `0` 待发送、`1` 成功、`2` 失败；日志查询和补偿任务都依赖该语义。
   */
  private Integer sendStatus;

  /**
   * 失败原因。
   * 仅在发送失败时有值，用于区分配置问题、网络问题和供应商业务拒绝。
   */
  private String failReason;

  /**
   * 重试次数。
   * 当前实现暂未自动重试，但预留该字段是为了后续任务补偿时保留幂等上下文。
   */
  private Integer retryCount;

  /**
   * 请求来源 IP。
   * 用于识别异常刷码行为；在代理部署场景下需要结合请求头解析真实客户端地址。
   */
  private String requestIp;

  /**
   * 发起平台标识。
   * 例如 `miniapp`、`h5`、`app`、`admin`，用于区分来源端并做风控分析。
   */
  private String platform;

  /**
   * 真实发送时间。
   * 仅在第三方发送调用完成后赋值，和 `createTime` 不一定相同。
   */
  private LocalDateTime sendTime;

  /**
   * 人工可读备注。
   * 用来记录状态迁移背后的业务原因，例如“邮箱验证码待发送”“发送失败”。
   */
  private String remark;

  /**
   * 创建时间。
   * 由 MyBatis Plus 自动填充，避免业务层忘记赋值导致审计链断裂。
   */
  @TableField(fill = FieldFill.INSERT)
  private LocalDateTime createTime;

  /**
   * 更新时间。
   * 在发送成功或失败回写时自动更新，便于判断最新状态。
   */
  @TableField(fill = FieldFill.INSERT_UPDATE)
  private LocalDateTime updateTime;

  /**
   * 逻辑删除标记。
   * 当前业务默认保留发送审计数据，因此一般不会物理删除。
   */
  private Integer isDeleted;
}
