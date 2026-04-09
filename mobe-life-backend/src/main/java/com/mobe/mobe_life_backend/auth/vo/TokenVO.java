/**
 * 核心职责：承载 token 刷新接口的响应结果。
 * 所属业务模块：认证中心 / 登录态维护。
 * 重要依赖关系或外部约束：字段命名需要与前端 token 更新逻辑保持一致。
 */
package com.mobe.mobe_life_backend.auth.vo;

import lombok.Data;

/**
 * Token 返回值。
 *
 * <p>设计初衷是把刷新 token 的响应裁剪成最小结果对象，避免接口语义与完整登录响应耦合。</p>
 */
@Data
public class TokenVO {

  /**
   * 新生成的 JWT。
   * 不允许为空；调用方应以它替换本地旧 token。
   */
  private String token;
}
