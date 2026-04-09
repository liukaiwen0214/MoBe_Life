/**
 * 核心职责：集中声明 Nuxt 应用的模块、运行时配置和开发代理。
 * 所属业务模块：前端基础设施 / 框架配置。
 * 重要依赖关系或外部约束：`apiBase` 与后端地址和部署代理强相关，改动后会直接影响全站请求流向。
 */
export default defineNuxtConfig({
  compatibilityDate: '2026-04-07',

  modules: ['@nuxt/ui', '@pinia/nuxt'],

  css: ['~/assets/css/main.css'],

  ui: {
    fonts: false,
  },

  icon: {
    provider: 'none',
    fallbackToApi: false,
    clientBundle: {
      scan: true,
    },
  },

  runtimeConfig: {
    public: {
      apiBase: process.env.NUXT_PUBLIC_API_BASE || '/api',
      appName: process.env.NUXT_PUBLIC_APP_NAME || 'MoBe Life',
    },
  },

  nitro: {
    devProxy: {
      // 开发代理保持 `/api` 前缀不变，是为了让本地和生产环境的请求代码保持同构。
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
    },
  },
})
