/**
 * 文件级注释：
 * 核心职责：封装基于 $fetch 的 HTTP 请求工具，统一处理 API 基础路径配置和错误日志输出。
 * 所属业务模块：前端基础设施层 - 网络请求模块。
 * 重要依赖：
 * - Nuxt 3 的 $fetch：底层基于 ofetch，提供 SSR 友好的请求能力
 * - useRuntimeConfig：读取 Nuxt 运行时配置（区分服务端/客户端）
 * 设计约束：
 * - 当前为简单封装，生产环境应扩展：Token 注入、统一错误处理、请求/响应拦截等
 */

/**
 * 请求工具组合式函数，提供类型安全的 HTTP 请求能力。
 *
 * 设计初衷：在 Nuxt 3 应用中统一封装 HTTP 请求逻辑，
 * 解决直接使用 $fetch 时代码重复和配置分散的问题。
 *
 * 在架构中的角色：基础设施层工具函数，被各页面和组件的组合式函数调用。
 *
 * 核心能力：
 * - 自动注入 baseURL（从运行时配置读取）
 * - 保持 $fetch 的完整类型推导能力
 * - 统一错误日志输出
 *
 * 线程安全性：无状态函数，每次调用独立，无并发问题。
 *
 * 使用示例：
 * ```ts
 * // 在组件中使用
 * const { request } = useRequest()
 * const user = await request<UserVO>('/api/user/current')
 * ```
 *
 * 待扩展功能（生产环境建议）：
 * - 请求拦截器：自动附加 Authorization Header
 * - 响应拦截器：统一处理 401/403 错误，自动刷新 Token
 * - 请求取消：支持 AbortController
 * - 重试机制：网络错误自动重试
 */
export const useRequest = () => {
  /**
   * 运行时配置对象，包含 public（客户端可访问）和私有配置。
   * 为什么使用 useRuntimeConfig：
   * - 支持环境变量注入（.env 文件）
   * - 服务端和客户端可配置不同值（SSR 场景）
   * - 敏感配置不会泄露到客户端
   */
  const config = useRuntimeConfig();

  /**
   * 发起 HTTP 请求的通用方法。
   *
   * 方法作用：基于 $fetch 封装，自动注入 baseURL，保持类型安全。
   *
   * @param url 请求路径，相对于 baseURL
   *            - 格式：以 / 开头的路径，如 '/api/user/current'
   *            - 约束：不包含 baseURL 部分
   * @param options $fetch 选项对象，与原生 $fetch 参数一致
   *                - 可选：method、headers、body、params 等
   *                - 类型：Parameters<typeof $fetch<T>>[1] 保持类型推导
   *
   * @returns Promise<T> 解析后的响应数据，类型由泛型 T 决定
   *          - 成功：返回后端 data 字段的解析结果
   *          - 失败：抛出异常，已在控制台打印错误日志
   *
   * 异常说明：
   * - 网络错误：抛出 FetchError，message 包含状态码和状态文本
   * - 业务错误（code ≠ 200）：当前直接抛出，建议扩展统一处理
   *
   * 副作用：
   * 1. 发起 HTTP 请求（客户端或服务端，取决于调用上下文）
   * 2. 请求失败时在控制台输出错误日志
   *
   * 类型设计：
   * - 使用泛型 T 保持响应数据的类型推导
   * - 与后端 Result<T> 结构对应，实际返回的是 Result.data
   */
  const request = async <T>(
    url: string,
    options?: Parameters<typeof $fetch<T>>[1],
  ) => {
    try {
      return await $fetch<T>(url, {
        // 从运行时配置读取 API 基础路径
        // 默认值：开发环境通常为 http://localhost:8080
        baseURL: config.public.apiBase,
        // 展开用户传入的其他选项（method、body、headers 等）
        ...options,
      });
    } catch (error) {
      // 统一错误日志输出，便于开发调试
      // 生产环境建议：接入 Sentry 等错误监控平台
      console.error("request error:", error);
      // 继续抛出异常，让调用方决定如何处理
      throw error;
    }
  };

  /**
   * 返回请求工具对象。
   * 设计为对象形式，便于未来扩展更多方法（如 get/post/put/delete 快捷方法）。
   */
  return {
    request,
  };
};
