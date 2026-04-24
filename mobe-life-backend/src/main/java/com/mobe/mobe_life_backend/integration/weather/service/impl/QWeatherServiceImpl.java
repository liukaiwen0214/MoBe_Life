/**
 * 核心职责：对接和风天气接口，根据经纬度查询城市与实时天气并转换为前端可直接使用的结构。
 * 所属业务模块：外部集成 / 天气服务实现。
 * 重要依赖关系或外部约束：依赖和风天气 API Key 与 Host 配置；远程接口可能返回 gzip 压缩体。
 */
package com.mobe.mobe_life_backend.integration.weather.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mobe.mobe_life_backend.common.exception.BusinessException;
import com.mobe.mobe_life_backend.integration.weather.config.QWeatherProperties;
import com.mobe.mobe_life_backend.integration.weather.service.WeatherService;
import com.mobe.mobe_life_backend.integration.weather.vo.WeatherInfoVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
@Slf4j
public class QWeatherServiceImpl implements WeatherService {

  private final QWeatherProperties qWeatherProperties;
  private final ObjectMapper objectMapper;

  /** JDK 原生 HTTP 客户端。当前请求量不大，用单例客户端即可满足需求。 */
  private final HttpClient httpClient = HttpClient.newHttpClient();

  /**
   * 根据经纬度获取实时天气。
   *
   * @param latitude 纬度。
   * @param longitude 经度。
   * @return 聚合后的天气信息。
   */
  @Override
  public WeatherInfoVO getWeatherByLocation(Double latitude, Double longitude) {
    try {
      validateConfig();

      String location = buildLocation(longitude, latitude);
      String city = fetchCityName(location);
      JsonNode weatherNow = fetchWeatherNow(location);

      WeatherInfoVO vo = new WeatherInfoVO();
      vo.setCity(StringUtils.hasText(city) ? city : "当前城市");
      vo.setWeatherText(weatherNow.path("text").asText("天气未知"));
      vo.setTemperature(weatherNow.path("temp").asText(""));
      vo.setIcon(weatherNow.path("icon").asText(""));

      return vo;
    } catch (BusinessException e) {
      throw e;
    } catch (Exception e) {
      log.error("Failed to fetch weather by location, latitude={}, longitude={}", latitude, longitude, e);
      throw new BusinessException(500, "获取天气失败：" + e.getMessage());
    }
  }

  /** 校验和风天气基础配置是否齐全。 */
  private void validateConfig() {
    if (!StringUtils.hasText(qWeatherProperties.getApiHost())
        || !StringUtils.hasText(qWeatherProperties.getApiKey())) {
      throw new BusinessException(500, "和风天气配置不完整");
    }
  }

  /** 把经纬度按和风天气要求格式化为 `经度,纬度` 字符串。 */
  private String buildLocation(Double longitude, Double latitude) {
    String lng = BigDecimal.valueOf(longitude)
        .setScale(6, RoundingMode.HALF_UP)
        .stripTrailingZeros()
        .toPlainString();
    String lat = BigDecimal.valueOf(latitude)
        .setScale(6, RoundingMode.HALF_UP)
        .stripTrailingZeros()
        .toPlainString();
    return lng + "," + lat;
  }

  /** 调用城市查询接口，把经纬度转换为更适合展示的城市名。 */
  private String fetchCityName(String location) throws Exception {
    String url = qWeatherProperties.getApiHost()
        + "/geo/v2/city/lookup?location="
        + URLEncoder.encode(location, StandardCharsets.UTF_8);

    HttpRequest request = HttpRequest.newBuilder()
        .GET()
        .header("Accept", "application/json")
        .header("Accept-Encoding", "gzip")
        .header("X-QW-Api-Key", qWeatherProperties.getApiKey())
        .uri(URI.create(url))
        .build();

    HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());
    String responseBody = decodeResponseBody(response.body());
    JsonNode root = objectMapper.readTree(responseBody);

    String code = root.path("code").asText();
    if (!"200".equals(code)) {
      throw new BusinessException(500, "查询城市信息失败");
    }

    JsonNode locationList = root.path("location");
    if (!locationList.isArray() || locationList.isEmpty()) {
      return "当前城市";
    }

    JsonNode first = locationList.get(0);
    String city = first.path("name").asText("");
    String adm2 = first.path("adm2").asText("");

    if (StringUtils.hasText(city)) {
      return city;
    }
    if (StringUtils.hasText(adm2)) {
      return adm2;
    }
    return "当前城市";
  }

  /** 调用实时天气接口，读取 `now` 节点。 */
  private JsonNode fetchWeatherNow(String location) throws Exception {
    String url = qWeatherProperties.getApiHost()
        + "/v7/weather/now?location="
        + URLEncoder.encode(location, StandardCharsets.UTF_8);

    HttpRequest request = HttpRequest.newBuilder()
        .GET()
        .header("Accept", "application/json")
        .header("Accept-Encoding", "gzip")
        .header("X-QW-Api-Key", qWeatherProperties.getApiKey())
        .uri(URI.create(url))
        .build();

    HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());
    String responseBody = decodeResponseBody(response.body());
    JsonNode root = objectMapper.readTree(responseBody);

    String code = root.path("code").asText();
    if (!"200".equals(code)) {
      throw new BusinessException(500, "查询天气失败");
    }

    JsonNode now = root.path("now");
    if (now.isMissingNode() || now.isNull()) {
      throw new BusinessException(500, "天气数据为空");
    }
    return now;
  }

  /** 按响应头实际内容解码 HTTP 响应体，兼容和风天气返回 gzip 压缩数据。 */
  private String decodeResponseBody(byte[] bodyBytes) throws Exception {
    if (bodyBytes == null || bodyBytes.length == 0) {
      return "";
    }

    // gzip magic number: 0x1f 0x8b
    boolean gzip = bodyBytes.length >= 2
        && (bodyBytes[0] == (byte) 0x1f)
        && (bodyBytes[1] == (byte) 0x8b);

    if (!gzip) {
      return new String(bodyBytes, StandardCharsets.UTF_8);
    }

    try (
        InputStream inputStream = new ByteArrayInputStream(bodyBytes);
        GZIPInputStream gzipInputStream = new GZIPInputStream(inputStream);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
      byte[] buffer = new byte[1024];
      int len;
      while ((len = gzipInputStream.read(buffer)) != -1) {
        outputStream.write(buffer, 0, len);
      }
      return outputStream.toString(StandardCharsets.UTF_8);
    }
  }
}
