/**
 * 集中声明 Nuxt 模块、运行时配置与开发代理。
 * 模块：前端基础设施 / 框架配置。
 * 约束：apiBase 与后端地址及部署代理强相关，改动会直接影响全站请求走向。
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
      '/api': {
        target: 'http://127.0.0.1:8080/api',
        changeOrigin: true,
      },
    },
  },
})
