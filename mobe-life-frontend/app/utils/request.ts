export const useRequest = () => {
  const config = useRuntimeConfig();

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
