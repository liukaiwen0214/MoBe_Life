/**
 * 核心职责：封装认证中心返回给前端或上层调用方的展示模型。
 * 所属业务模块：认证中心 / VO。
 * 重要依赖关系或外部约束：字段设计更偏向展示与接口契约，不一定与数据库结构一一对应。
 */
package com.mobe.mobe_life_backend.auth.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "图形验证码响应对象，用于返回验证码标识与图片内容")
@Data
public class CaptchaVO {

  @Schema(description = "验证码标识", example = "captcha_123456")
  private String captchaKey;

  @Schema(description = "验证码图片 Base64 字符串", example = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAA...")
  private String captchaImage;
}
