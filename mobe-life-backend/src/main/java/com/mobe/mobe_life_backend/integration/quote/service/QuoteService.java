package com.mobe.mobe_life_backend.integration.quote.service;

import com.mobe.mobe_life_backend.integration.quote.vo.QuoteInfoVO;

public interface QuoteService {

  /**
   * 获取每日一句
   *
   * @return 每日一句信息
   */
  QuoteInfoVO getDailyQuote();
}