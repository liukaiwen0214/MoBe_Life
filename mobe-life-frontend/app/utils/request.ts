/**
 * 核心职责：封装 Nuxt 侧 HTTP 请求入口，统一注入 API 基础地址并保留类型推导。
 * 所属业务模块：前端基础设施 / 网络请求。
 * 重要依赖关系或外部约束：当前仅做最轻量封装，尚未接入 token 注入和统一业务码解析。
 */
export const useRequest = () => {
  const config = useRuntimeConfig();

  /**
   * 通用请求方法。
   *
   * @param url 相对路径，不包含 `apiBase` 前缀。
   * @param options 原始 `$fetch` 配置，允许为空。
   * @returns 请求结果。
   * @throws Error 当网络请求失败时抛出原始异常。
   */
  const request = async <T>(
    url: string,
    options?: Parameters<typeof $fetch<T>>[1],
  ) => {
    try {
      return await $fetch<T>(url, {
        baseURL: config.public.apiBase,
        ...options,
      });
    } catch (error) {
      console.error("request error:", error);
      throw error;
    }
  };

  return {
    request,
  };
};
