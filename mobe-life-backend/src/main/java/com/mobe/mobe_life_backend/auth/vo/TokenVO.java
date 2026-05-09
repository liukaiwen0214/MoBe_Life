/**
 * 封装认证中心对外输出的视图字段。
 * 模块：认证中心 / VO。
 * 约束：字段设计更偏向展示与接口契约，不一定与数据库结构一一对应。
 */
package com.mobe.mobe_life_backend.auth.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "令牌响应对象，用于返回刷新后的访问令牌")
@Data
public class TokenVO {

  @Schema(description = "新的访问令牌", example = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjF9.newsignature")
  private String token;
}
