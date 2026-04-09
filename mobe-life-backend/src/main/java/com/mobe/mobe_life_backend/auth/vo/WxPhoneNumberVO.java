/**
 * 核心职责：承载微信手机号接口解析后的结果。
 * 所属业务模块：认证中心 / 第三方平台返回值。
 * 重要依赖关系或外部约束：主要字段由业务代码手动组装，不完全等同于微信原始响应结构。
 */
package com.mobe.mobe_life_backend.auth.vo;

import lombok.Data;

/**
 * 微信手机号信息。
 *
 * <p>设计初衷是把业务实际需要的手机号字段从微信原始 `phone_info` 中提炼出来，
 * 避免上层服务反复操作 JSON 对象。</p>
 */
@Data
public class WxPhoneNumberVO {

  /**
   * 完整手机号。
   * 可能包含国家区号前缀，适合做展示，不一定适合作为数据库唯一值。
   */
  private String phoneNumber;

  /**
   * 去区号后的纯手机号。
   * 当前绑定逻辑使用该字段作为账号唯一手机号。
   */
  private String purePhoneNumber;

  /**
   * 国家区号。
   * 当前国内业务通常为 `86`，但保留该字段便于国际化扩展。
   */
  private String countryCode;

  /**
   * 预留错误码字段。
   * 当前成功流程不会使用；如果未来直接映射微信原始响应，可避免结构变更。
   */
  private Integer errcode;

  /**
   * 预留错误信息字段。
   * 当前主要作为兼容扩展字段存在。
   */
  private String errmsg;
}
