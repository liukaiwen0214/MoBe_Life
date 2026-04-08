package com.mobe.mobe_life_backend.config;

import lombok.RequiredArgsConstructor;
import com.mobe.mobe_life_backend.file.config.FileUploadProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

  private final JwtInterceptor jwtInterceptor;
  private final FileUploadProperties fileUploadProperties;

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(jwtInterceptor)
        .addPathPatterns("/api/**")
        .excludePathPatterns(
            "/api/auth/wx-mini-login",
            "/api/auth/refresh-token",
            "/test",
            "/test/error");
  }

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/uploads/**")
        .addResourceLocations("file:" + fileUploadProperties.getPath() + "/");
  }
}