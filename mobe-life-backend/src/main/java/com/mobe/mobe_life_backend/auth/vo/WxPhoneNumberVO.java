/**
 * 核心职责：封装认证中心返回给前端或上层调用方的展示模型。
 * 所属业务模块：认证中心 / VO。
 * 重要依赖关系或外部约束：字段设计更偏向展示与接口契约，不一定与数据库结构一一对应。
 */
package com.mobe.mobe_life_backend.auth.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "微信手机号响应对象，用于承接微信手机号授权解析结果")
@Data
public class WxPhoneNumberVO {

  @Schema(description = "完整手机号", example = "+8613812345678")
  private String phoneNumber;

  @Schema(description = "纯手机号", example = "13812345678")
  private String purePhoneNumber;

  @Schema(description = "国家区号", example = "86")
  private String countryCode;

  @Schema(description = "微信错误码", example = "0")
  private Integer errcode;

  @Schema(description = "微信错误信息", example = "ok")
  private String errmsg;
}
