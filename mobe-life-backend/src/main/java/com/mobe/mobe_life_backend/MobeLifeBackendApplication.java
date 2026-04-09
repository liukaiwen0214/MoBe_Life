/**
 * 文件级注释：
 * 核心职责：MoBe 生活管理系统后端服务的启动入口，负责初始化 Spring Boot 应用上下文并启动嵌入式 Web 容器。
 * 所属业务模块：系统基础设施层 - 应用启动模块。
 * 重要依赖：Spring Boot 自动配置机制、嵌入式 Tomcat/Jetty 容器。
 *
 * 设计说明：
 * 1. 采用 @SpringBootApplication 组合注解，隐式启用自动配置、组件扫描和配置属性绑定。
 * 2. 启动类位于 com.mobe.mobe_life_backend 包根目录，确保能扫描到所有子包中的 Spring 组件。
 * 3. 生产环境通过 start.sh 脚本启动，支持优雅关闭和 PID 文件管理。
 */
package com.mobe.mobe_life_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * MoBe 生活管理系统后端应用主类。
 *
 * <p>设计初衷：作为整个后端服务的唯一入口点，遵循 Spring Boot 约定优于配置的设计理念。
 * 在架构中的角色：应用生命周期管理器，负责引导整个 Spring 容器和依赖注入环境。</p>
 *
 * <p>线程安全性：该类仅在主线程中执行一次，不存在并发安全问题。</p>
 *
 * <p>使用示例：
 * <pre>
 *   // 开发环境直接运行
 *   ./mvnw spring-boot:run
 *
 *   // 生产环境通过脚本启动
 *   ./start.sh start
 * </pre></p>
 */
@SpringBootApplication
public class MobeLifeBackendApplication {

  /**
   * 应用主入口方法。
   *
   * <p>方法作用：初始化 Spring 应用上下文，加载所有配置类、Bean 定义和外部化配置，
   * 启动嵌入式 Web 服务器并监听配置端口。</p>
   *
   * @param args 命令行参数，可传入 --spring.profiles.active=prod 等 Spring Boot 标准参数
   *             支持通过 --server.port=8081 覆盖默认端口
   *
   * <p>副作用：
   * 1. 创建并刷新 Spring ApplicationContext
   * 2. 启动嵌入式 Tomcat 服务器（默认端口 8080）
   * 3. 触发所有 CommandLineRunner 和 ApplicationRunner 实现
   * 4. 向 JVM 注册 ShutdownHook 以支持优雅关闭</p>
   *
   * <p>异常说明：若端口被占用或配置加载失败，将抛出 ApplicationContextException 并终止 JVM。</p>
   */
  public static void main(String[] args) {
    SpringApplication.run(MobeLifeBackendApplication.class, args);
  }

}
