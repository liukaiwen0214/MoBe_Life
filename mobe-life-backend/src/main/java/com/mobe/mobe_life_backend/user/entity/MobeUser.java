/**
 * 文件级注释：
 * 核心职责：定义 MoBe 生活管理系统用户实体的数据结构和持久化映射关系。
 * 所属业务模块：用户中心 (User Center) - 领域模型层。
 * 重要依赖：
 * - MyBatis-Plus：ORM 框架，提供注解式数据库映射和通用 CRUD
 * - Lombok @Data：自动生成 getter/setter/equals/hashCode/toString
 * 数据库约束：
 * - 表名：mobe_user（使用 mobe_ 前缀避免与系统保留字冲突）
 * - 字符集：utf8mb4（支持 emoji 和完整 Unicode）
 * - 存储引擎：InnoDB（支持事务和外键）
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
 * 用户实体类，映射 mobe_user 数据库表。
 *
 * <p>设计初衷：作为用户领域的核心领域模型（Domain Model），
 * 封装用户相关的所有属性和行为，实现数据与业务逻辑的封装。</p>
 *
 * <p>在架构中的角色：数据访问层（DAO/Repository Layer）与业务逻辑层的桥梁，
 * - 向上（Service）：提供类型安全的业务数据对象
 * - 向下（Mapper）：通过 MyBatis-Plus 注解映射数据库结构</p>
 *
 * <p>核心业务概念：
 * - openid：微信用户在当前小程序的唯一标识，微信登录的核心凭证
 * - unionid：微信用户在同一主体（企业）下的唯一标识，跨应用关联用户
 * - status：账号状态机（0-正常，1-禁用），用于软删除和账号封禁
 * - gender：性别枚举（0-未知，1-男，2-女），符合微信用户数据规范</p>
 *
 * <p>线程安全性：该类为纯数据载体（POJO），实例非线程安全，
 * 不应在多线程间共享可变实例，建议通过 Service 层事务控制并发访问。</p>
 *
 * <p>使用示例：
 * <pre>
 *   // 创建新用户
 *   MobeUser user = new MobeUser();
 *   user.setOpenid(wxOpenid);
 *   user.setNickname("张三");
 *   user.setStatus(0);
 *   userMapper.insert(user);
 *
 *   // 查询用户
 *   MobeUser user = userMapper.selectById(10086L);
 *   String phone = user.getPhone();
 * </pre></p>
 */
@Data
@TableName("mobe_user")
public class MobeUser {

  /**
   * 用户唯一标识，主键，自增。
   *
   * <p>生成策略：数据库自增（AUTO_INCREMENT），确保全局唯一且有序。
   * 使用 Long 类型（对应 MySQL BIGINT），支持亿级用户量。</p>
   *
   * <p>业务规则：
   * - 创建时无需设置，由数据库自动生成
   * - 插入后可通过 user.getId() 获取生成值（MyBatis-Plus 自动回填）</p>
   */
  @TableId(type = IdType.AUTO)
  private Long id;

  /**
   * 微信 OpenID，用户在当前小程序的唯一标识。
   * 为什么使用：微信登录时，通过 code2session 接口获取，用于识别用户身份。
   * 约束：每个小程序的 OpenID 不同，同一用户在不同小程序有不同 OpenID。
   */
  private String openid;

  /**
   * 微信 UnionID，用户在同一微信开放平台账号下的唯一标识。
   * 为什么使用：用于跨小程序/公众号识别同一用户，实现账号互通。
   * 获取条件：需将小程序绑定到微信开放平台账号。
   * 可为空：未绑定开放平台账号时，微信不返回 UnionID。
   */
  private String unionid;

  /**
   * 手机号，中国大陆手机号码。
   * 格式：11 位数字，1 开头。
   * 来源：通过微信 getPhoneNumber 组件获取加密数据后解密得到。
   * 可为空：用户未绑定手机号时为 null。
   * 唯一性：逻辑上应唯一，但数据库未加唯一索引（支持多账号绑定同一手机号场景）。
   */
  private String phone;

  /**
   * 邮箱地址，用于账号找回和接收通知。
   * 格式：符合 RFC 5322 标准。
   * 验证方式：绑定邮箱时需验证验证码。
   * 可为空：用户未绑定邮箱时为 null。
   * 唯一性：数据库应加唯一索引（防止多账号绑定同一邮箱）。
   */
  private String email;

  /**
   * 密码哈希，使用 BCrypt 算法加密存储。
   * 为什么使用 BCrypt：
   * - 自带 salt，无需额外存储
   * - 可配置工作因子（当前强度 10），抵御暴力破解
   * - 业界标准，Spring Security 默认支持
   * 可为空：微信一键登录用户初始无密码，需主动设置。
   * 安全：禁止存储明文密码，日志中不得输出此字段。
   */
  private String password;

  /**
   * 用户昵称，显示名称。
   * 来源：
   * - 首次登录时从微信获取（需用户授权）
   * - 用户可在个人中心修改
   * 约束：长度限制 50 字符，支持 emoji（utf8mb4）。
   * 可为空：用户未设置或拒绝授权时为 null。
   */
  private String nickname;

  /**
   * 头像 URL，用户头像图片地址。
   * 来源：
   * - 首次登录时从微信获取（需用户授权）
   * - 用户可上传自定义头像
   * 格式：
   * - 微信头像：微信 CDN 地址（有有效期，需定期刷新）
   * - 自定义头像：本系统文件服务地址
   * 可为空：用户未设置或拒绝授权时为 null。
   */
  private String avatar;

  /**
   * 性别，枚举值。
   * 取值规范（与微信一致）：
   * - 0：未知（默认，用户未设置或拒绝授权）
   * - 1：男性
   * - 2：女性
   * 为什么用 Integer 而非 Enum：
   * - 数据库存储更简洁（TINYINT vs VARCHAR）
   * - 兼容微信原始数据格式，避免转换层
   */
  private Integer gender;

  /**
   * 出生日期，用于年龄计算和生日提醒。
   * 格式：yyyy-MM-dd（LocalDate）。
   * 来源：用户主动填写。
   * 可为空：用户未设置时为 null。
   * 业务用途：
   * - 计算用户年龄
   * - 生日当天推送祝福消息
   */
  private LocalDate birthday;

  /**
   * 账号状态，用于软删除和封禁。
   * 取值规范：
   * - 0：正常（默认）
   * - 1：禁用（无法登录，保留数据）
   * 为什么不用逻辑删除字段：
   * - isDeleted 用于物理删除标记
   * - status 用于业务状态控制（可扩展更多状态，如 2-审核中）
   */
  private Integer status;

  /**
   * 记录创建时间，自动填充。
   * 填充策略：INSERT 时自动设置为当前时间（MyBatis-Plus @TableField(fill = FieldFill.INSERT)）。
   * 用途：
   * - 数据分析：用户增长趋势
   * - 审计追踪：记录创建时点
   * 不可修改：业务代码不应手动设置此字段。
   */
  @TableField(fill = FieldFill.INSERT)
  private LocalDateTime createTime;

  /**
   * 记录更新时间，自动填充。
   * 填充策略：INSERT 和 UPDATE 时自动设置为当前时间。
   * 用途：
   * - 数据同步：判断记录是否变更
   * - 缓存失效：根据更新时间决定缓存是否过期
   * 不可修改：业务代码不应手动设置此字段。
   */
  @TableField(fill = FieldFill.INSERT_UPDATE)
  private LocalDateTime updateTime;

  /**
   * 逻辑删除标记，MyBatis-Plus 逻辑删除字段。
   * 取值规范：
   * - 0：未删除（默认）
   * - 1：已删除
   * 作用：
   * - 软删除，保留数据用于审计和恢复
   * - MyBatis-Plus 自动过滤已删除记录（查询时自动加 WHERE is_deleted = 0）
   * 注意：与 status 字段区分，isDeleted 是技术字段，status 是业务字段。
   */
  private Integer isDeleted;

  /**
   * 备注字段，用于存储额外信息。
   * 用途：
   * - 运营备注：标记特殊用户（如 VIP、测试账号）
   * - 扩展字段：临时存储未建模的业务数据
   * 约束：长度限制 500 字符。
   * 可为空：无备注时为 null。
   */
  private String remark;
}
