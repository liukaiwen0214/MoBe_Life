/**
 * 核心职责：维护应用级共享状态。
 * 所属业务模块：前端基础设施 / 状态管理。
 * 重要依赖关系或外部约束：当前 store 很轻，只负责应用品牌信息，后续扩展时要避免把页面级临时状态堆进全局 store。
 */
import { defineStore } from "pinia";

/**
 * 应用级 store。
 *
 * 设计初衷是给布局和首页这类跨页面组件提供稳定的全局品牌信息来源。
 */
export const useAppStore = defineStore("app", {
  state: () => ({
    appName: "MoBe Life",
  }),

  getters: {},

  actions: {},
});
