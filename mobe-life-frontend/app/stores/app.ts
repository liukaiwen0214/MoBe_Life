/**
 * 维护应用级共享状态。
 * 模块：前端基础设施 / 状态管理。
 * 约束：当前 store 很轻，只放品牌类信息；扩展时避免把页面级临时状态塞进全局 store。
 */
import { defineStore } from "pinia";

/**
 * 应用级 store。
 *
 * <p>为布局与首页等跨页组件提供稳定的全局品牌信息来源，
 * 避免各页面重复硬编码应用名。</p>
 */
export const useAppStore = defineStore("app", {
  state: () => ({
    // 应用品牌名通常会被导航栏、首页欢迎语和 SEO 元信息复用。
    appName: "MoBe Life",
    theme: "light" as "light" | "dark",
  }),

  getters: {
    isDarkTheme: (state) => state.theme === "dark",
  },

  actions: {
    toggleTheme() {
      this.theme = this.theme === "dark" ? "light" : "dark";
    },
  },
});
