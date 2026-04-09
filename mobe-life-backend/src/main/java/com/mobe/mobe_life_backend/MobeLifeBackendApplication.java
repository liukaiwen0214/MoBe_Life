/**
 * 核心职责：作为后端应用启动入口，负责引导 Spring Boot 容器启动。
 * 所属业务模块：系统基础设施 / 启动引导。
 * 重要依赖关系或外部约束：启动类位于根包下，保证组件扫描能覆盖认证、用户、文件等全部子模块。
 */
package com.mobe.mobe_life_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 后端应用主类。
 *
 * <p>设计初衷是把应用启动责任保持在一个极小而稳定的入口点，
 * 避免把任何业务初始化逻辑塞进 `main` 方法，降低启动过程与业务代码耦合。</p>
 *
 * <p>线程安全性：仅在 JVM 启动阶段由主线程执行一次，无并发共享状态问题。</p>
 */
@SpringBootApplication
public class MobeLifeBackendApplication {

  /**
   * 应用主入口。
   *
   * @param args 命令行参数，允许为空；可用于传入 Spring Profile、端口和外部配置覆盖项。
   * @implNote 该方法会启动整个 Spring 上下文和嵌入式 Web 容器。
   */
  public static void main(String[] args) {
    SpringApplication.run(MobeLifeBackendApplication.class, args);
  }
}
