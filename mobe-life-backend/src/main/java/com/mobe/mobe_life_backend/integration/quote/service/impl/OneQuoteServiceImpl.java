/**
 * 核心职责：承载外部集成相关代码，是该模块实现中的一个组成单元。
 * 所属业务模块：外部集成 / quote。
 * 重要依赖关系或外部约束：需要与同模块中的控制层、服务层和数据结构保持语义一致。
 */
package com.mobe.mobe_life_backend.integration.quote.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mobe.mobe_life_backend.common.exception.BusinessException;
import com.mobe.mobe_life_backend.integration.quote.config.OneQuoteProperties;
import com.mobe.mobe_life_backend.integration.quote.service.QuoteService;
import com.mobe.mobe_life_backend.integration.quote.vo.QuoteInfoVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
@RequiredArgsConstructor
public class OneQuoteServiceImpl implements QuoteService {

  private final OneQuoteProperties oneQuoteProperties;
  private final ObjectMapper objectMapper;

  private final HttpClient httpClient = HttpClient.newHttpClient();

  /**
   * 获取DailyQuote。
   *
   * @return 返回对应结果。
   */
  @Override
  public QuoteInfoVO getDailyQuote() {
    try {
      if (!StringUtils.hasText(oneQuoteProperties.getApiUrl())) {
        throw new BusinessException(500, "ONE 接口地址未配置");
      }

      HttpRequest request = HttpRequest.newBuilder()
          .GET()
          .uri(URI.create(oneQuoteProperties.getApiUrl()))
          .build();

      HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
      JsonNode root = objectMapper.readTree(response.body());

      int res = root.path("res").asInt(-1);
      if (res != 0) {
        throw new BusinessException(500, "获取每日一句失败");
      }

      JsonNode firstItem = root.path("data").path("content_list");
      if (!firstItem.isArray() || firstItem.isEmpty()) {
        throw new BusinessException(500, "每日一句数据为空");
      }

      JsonNode item = firstItem.get(0);

      QuoteInfoVO vo = new QuoteInfoVO();
      vo.setText(item.path("forward").asText("把今天最重要的一件事，轻轻落下来。"));
      vo.setFrom(buildFromText(item));
      vo.setImageUrl(item.path("img_url").asText(""));

      return vo;
    } catch (BusinessException e) {
      throw e;
    } catch (Exception e) {
      throw new BusinessException(500, "获取每日一句失败");
    }
  }

  private String buildFromText(JsonNode item) {
    String wordsInfo = item.path("words_info").asText("");
    String title = item.path("title").asText("");

    if (StringUtils.hasText(wordsInfo)) {
      return wordsInfo;
    }
    if (StringUtils.hasText(title)) {
      return title;
    }
    return "ONE";
  }
}
