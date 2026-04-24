/**
 * 核心职责：承载外部集成相关代码，是该模块实现中的一个组成单元。
 * 所属业务模块：外部集成 / quote。
 * 重要依赖关系或外部约束：需要与同模块中的控制层、服务层和数据结构保持语义一致。
 */
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