/**
 * 核心职责：统一配置跨域访问策略。
 * 所属业务模块：系统基础设施 / Web 安全配置。
 * 重要依赖关系或外部约束：跨域策略需要与前端部署方式匹配；当前配置偏开发友好，生产环境应进一步收敛来源域名。
 */
package com.mobe.mobe_life_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;

/**
 * CORS 配置。
 *
 * <p>设计初衷是把跨域放行规则集中管理，避免控制器层零散地声明跨域策略，导致行为难以追踪。</p>
 *
 * <p>线程安全性：配置对象在启动时构建，运行期按只读方式使用，线程安全。</p>
 */
@Configuration
public class CorsConfig {

  /**
   * 构建全局跨域配置源。
   *
   * @return 跨域配置源，不返回 null；对所有路径生效。
   * @implNote 当前使用 `allowedOriginPatterns("*")` 配合 `allowCredentials(false)`，
   * 适合前后端分离开发阶段；若未来需要携带 Cookie，则必须显式收敛允许来源。
   */
  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();

    configuration.setAllowedOriginPatterns(List.of("*"));
    configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    configuration.setAllowedHeaders(List.of("*"));
    configuration.setAllowCredentials(false);
    configuration.setMaxAge(3600L);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
}
