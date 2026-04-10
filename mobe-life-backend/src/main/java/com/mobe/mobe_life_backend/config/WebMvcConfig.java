/**
 * 核心职责：集中注册 MVC 相关的拦截器和静态资源映射。
 * 所属业务模块：系统基础设施 / Web MVC 配置。
 * 重要依赖关系或外部约束：认证白名单路径、文件上传目录与前端访问路径都依赖这里的配置保持一致。
 */
package com.mobe.mobe_life_backend.config;

import com.mobe.mobe_life_backend.file.config.FileUploadProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * MVC 配置类。
 *
 * <p>
 * 设计初衷是把“请求如何进入业务”和“本地文件如何对外暴露”集中定义，
 * 避免相关配置散落在多个配置类中难以维护。
 * </p>
 */
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

  /** JWT 鉴权拦截器。 */
  private final JwtInterceptor jwtInterceptor;

  /** 文件上传配置。 */
  private final FileUploadProperties fileUploadProperties;

  /**
   * 注册拦截器。
   *
   * @param registry 拦截器注册表，不允许为 null。
   * @implNote 当前默认保护 `/api/**`，只对白名单接口放行。
   */
  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(jwtInterceptor)
        .addPathPatterns("/api/**")
        .excludePathPatterns(
            "/api/auth/wx-mini-login",
            "/api/auth/refresh-token",
            "/api/auth/captcha",
            "/api/auth/password-login",
            "/api/auth/email-login",
            "/api/auth/code-login",
            "/api/auth/send-login-email-code",
            "/test",
            "/test/error");
  }

  /**
   * 注册静态资源映射。
   *
   * @param registry 资源映射注册表，不允许为 null。
   * @implNote 当前把本地上传目录暴露到 `/uploads/**`，这样头像 URL 可以直接被前端访问。
   */
  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/uploads/**")
        .addResourceLocations("file:" + fileUploadProperties.getPath() + "/");
  }
}
