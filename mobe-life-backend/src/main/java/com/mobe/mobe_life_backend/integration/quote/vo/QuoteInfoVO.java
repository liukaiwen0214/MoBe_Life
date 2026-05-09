/**
 * 承载外部集成实现，作为该模块的一个组成单元。
 * 模块：外部集成 / quote。
 * 约束：需要与同模块中的控制层、服务层和数据结构保持语义一致。
 */
package com.mobe.mobe_life_backend.integration.quote.vo;

import lombok.Data;

@Data
public class QuoteInfoVO {

  /**
   * 文案内容
   */
  private String text;

  /**
   * 出处
   */
  private String from;

  /**
   * 配图地址
   */
  private String imageUrl;
}