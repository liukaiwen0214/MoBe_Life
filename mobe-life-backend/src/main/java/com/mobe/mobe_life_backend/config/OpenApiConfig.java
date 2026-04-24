/**
 * 核心职责：承载系统配置相关代码，是该模块实现中的一个组成单元。
 * 所属业务模块：系统配置 / OpenApiConfig.java。
 * 重要依赖关系或外部约束：需要与同模块中的控制层、服务层和数据结构保持语义一致。
 */
package com.mobe.mobe_life_backend.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

        /**
         * 执行mobeOpenApi。
         */
        @Bean
        public OpenAPI mobeOpenApi() {
                return new OpenAPI()
                                .info(new Info()
                                                .title("MoBe Life API 文档")
                                                .description("MoBe Life 小程序 / 网页端后端接口文档")
                                                .version("v1.0.0")
                                                .contact(new Contact().name("刘凯文"))
                                                .license(new License().name("Internal Use")))
                                .externalDocs(new ExternalDocumentation().description("MoBe Life Project"))

                                // ----- 下面这段是 YAML 很难搞定的：全局 Token 配置 -----
                                // 1. 定义一个安全方案（右上角有个 Authorize 按钮，点击弹出的输入框）
                                .components(new io.swagger.v3.oas.models.Components()
                                                .addSecuritySchemes("BearerToken",
                                                                new SecurityScheme()
                                                                                .type(SecurityScheme.Type.HTTP)
                                                                                .scheme("bearer")
                                                                                .bearerFormat("JWT")
                                                                                .description("请输入登录返回的 Token")))
                                // 2. 将这个安全方案全局应用到所有接口
                                .addSecurityItem(new SecurityRequirement().addList("BearerToken"));
        }
}
