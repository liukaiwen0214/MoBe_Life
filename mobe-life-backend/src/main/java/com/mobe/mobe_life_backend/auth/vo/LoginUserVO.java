/**
 * 核心职责：向客户端返回登录后的基础身份信息与访问令牌。
 * 所属业务模块：认证中心 / 登录响应。
 * 重要依赖关系或外部约束：字段结构需与前端登录态初始化逻辑保持一致。
 */
package com.mobe.mobe_life_backend.auth.vo;

import lombok.Data;

/**
 * 登录结果对象。
 *
 * <p>设计初衷是让小程序在一次登录响应中同时拿到“展示当前用户所需的最小资料”和“后续接口访问凭证”，
 * 减少登录后立刻再请求一次用户信息接口的往返成本。</p>
 */
@Data
public class LoginUserVO {

  /**
   * 当前登录用户主键。
   * 不会为空；前端通常会以此作为登录态内存标识。
   */
  private Long userId;

  /**
   * 用户昵称。
   * 允许为空，但当前新注册微信用户会被初始化为默认昵称“微信用户”。
   */
  private String nickname;

  /**
   * 用户头像地址。
   * 允许为空；前端需要自行准备兜底头像。
   */
  private String avatar;

  /**
   * 访问令牌。
   * 当前实现只返回单个 JWT；客户端后续请求需要通过 `Authorization` 头携带。
   */
  private String token;
}
