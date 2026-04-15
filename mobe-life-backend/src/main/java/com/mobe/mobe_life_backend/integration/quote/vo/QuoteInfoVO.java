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