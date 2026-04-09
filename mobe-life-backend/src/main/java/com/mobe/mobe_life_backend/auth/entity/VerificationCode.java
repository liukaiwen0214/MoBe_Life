/**
 * 核心职责：承载验证码生命周期数据，用于校验绑定邮箱等敏感操作。
 * 所属业务模块：认证中心 / 验证码体系。
 * 重要依赖关系或外部约束：字段语义需要与 `verification_code` 表保持一致；验证码明文不落库，只存摘要。
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
 * 验证码实体。
 *
 * <p>设计初衷是让验证码具备独立生命周期：生成、发送、校验、失效、作废都可追踪，
 * 从而支持频控、一次性消费和失败计数，而不是把验证码简单塞进缓存后失去审计能力。</p>
 *
 * <p>线程安全性：实体对象不是线程安全结构，只应在当前事务上下文中使用。</p>
 */
@Data
@TableName("verification_code")
public class VerificationCode {

  /**
   * 主键。
   * 使用数据库自增，便于按生成顺序查询“最新一条有效验证码”。
   */
  @TableId(type = IdType.AUTO)
  private Long id;

  /**
   * 验证目标。
   * 当前可为手机号或邮箱，和 `targetType` 组合后才能唯一界定验证码归属。
   */
  private String target;

  /**
   * 目标类型。
   * 当前约定 `1` 表示手机号、`2` 表示邮箱；整数编码便于和历史表、索引兼容。
   */
  private Integer targetType;

  /**
   * 业务场景编码。
   * 例如 `BIND_EMAIL`；同一目标在不同业务场景下必须使用不同验证码，避免跨场景串用。
   */
  private String bizType;

  /**
   * 验证码摘要。
   * 当前实现使用目标、业务类型和验证码一起哈希，避免数据库泄漏后直接暴露验证码明文。
   */
  private String codeHash;

  /**
   * 验证码脱敏预览。
   * 用于后台排障或审计时确认是否命中同一批验证码，不可反推出完整验证码。
   */
  private String codePreview;

  /**
   * 验证码状态。
   * 当前约定 `0` 未使用、`1` 已使用、`2` 已过期、`3` 已作废；状态迁移由服务层统一控制。
   */
  private Integer status;

  /**
   * 过期时间。
   * 到达该时间后即使验证码输入正确也不能继续使用。
   */
  private LocalDateTime expireTime;

  /**
   * 使用时间。
   * 仅在验证码校验成功时写入，用于审计“验证码何时被消费”。
   */
  private LocalDateTime usedTime;

  /**
   * 发送时间。
   * 用于判断发送频率限制，当前实现按“最近发送时间 + 60 秒”防刷。
   */
  private LocalDateTime sendTime;

  /**
   * 发起平台。
   * 便于分析验证码请求主要来自哪个入口，也为多端风控预留维度。
   */
  private String platform;

  /**
   * 模板编号。
   * 部分场景可用来关联具体短信或邮件模板，当前邮箱绑定流程暂未强依赖该字段。
   */
  private String templateCode;

  /**
   * 业务细分场景键。
   * 预留字段，用于在同一 `bizType` 下做更细粒度区分。
   */
  private String sceneKey;

  /**
   * 请求来源 IP。
   * 预留给更严格的风控规则，例如同一 IP 在短时间内多目标刷码。
   */
  private String requestIp;

  /**
   * 设备标识。
   * 预留给多端设备风控，目前业务未强依赖。
   */
  private String deviceId;

  /**
   * 校验失败次数。
   * 用于统计用户输错次数，也为未来增加输错锁定策略预留基础数据。
   */
  private Integer failCount;

  /**
   * 关联的消息日志主键。
   * 让验证码与具体发送记录形成闭环，便于定位“验证码生成了但邮件没发出去”的问题。
   */
  private Long messageLogId;

  /**
   * 人工可读备注。
   * 常用于记录作废、过期或成功绑定的业务原因。
   */
  private String remark;

  /**
   * 创建时间。
   * 由 MyBatis Plus 自动填充。
   */
  @TableField(fill = FieldFill.INSERT)
  private LocalDateTime createTime;

  /**
   * 更新时间。
   * 状态变化后自动更新，便于后续补偿任务扫描。
   */
  @TableField(fill = FieldFill.INSERT_UPDATE)
  private LocalDateTime updateTime;

  /**
   * 逻辑删除标记。
   * 当前主要用于兼容统一数据规范，业务上通常通过状态字段而非删除来表达生命周期结束。
   */
  private Integer isDeleted;
}
