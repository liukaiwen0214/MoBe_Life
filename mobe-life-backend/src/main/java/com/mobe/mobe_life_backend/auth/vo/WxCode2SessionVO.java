/**
 * 核心职责：映射微信 `code2session` 接口响应。
 * 所属业务模块：认证中心 / 第三方平台返回值。
 * 重要依赖关系或外部约束：字段命名需匹配微信官方 JSON 响应，尤其是下划线字段不能随意重命名。
 */
package com.mobe.mobe_life_backend.auth.vo;

import lombok.Data;

/**
 * 微信登录换取会话结果。
 *
 * <p>设计初衷是把微信返回的原始结构直接映射为 VO，减少手写 JSON 取值的错误概率。</p>
 */
@Data
public class WxCode2SessionVO {

  /**
   * 小程序用户在当前应用下的唯一标识。
   * 登录建档和后续身份关联都依赖该字段，成功场景下不应为空。
   */
  private String openid;

  /**
   * 微信会话密钥。
   * 当前业务未持久化，但保留字段便于以后扩展更多微信解密能力。
   */
  private String session_key;

  /**
   * 微信开放平台统一用户标识。
   * 可能为空；只有用户与开放平台账号体系完成关联时才会返回。
   */
  private String unionid;

  /**
   * 微信错误码。
   * 为 `null` 或 `0` 代表请求成功，非零时应结合 `errmsg` 直接失败处理。
   */
  private Integer errcode;

  /**
   * 微信错误信息。
   * 仅在失败场景下有意义，用于拼接业务异常信息帮助排障。
   */
  private String errmsg;
}
