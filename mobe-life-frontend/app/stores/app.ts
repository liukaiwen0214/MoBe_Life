/**
 * 文件级注释：
 * 核心职责：定义应用级别的全局状态管理，存储跨页面共享的数据（如应用名称、主题配置等）。
 * 所属业务模块：前端状态管理层 - 全局 Store。
 * 重要依赖：
 * - Pinia：Vue 官方推荐的状态管理库，支持 TypeScript 和 DevTools
 * 设计约束：
 * - 遵循 Pinia 的 Setup Store 语法（defineStore）
 * - 状态变更应通过 actions 进行，保持可预测性
 */

import { defineStore } from "pinia";

/**
 * 应用全局状态 Store。
 *
 * 设计初衷：集中管理应用级别的共享状态，避免 props drilling 和事件总线的复杂性，
 * 提供响应式、可追踪的状态变更机制。
 *
 * 在架构中的角色：状态管理层（State Management Layer），
 * 作为单一数据源（Single Source of Truth）供各组件读取和修改全局状态。
 *
 * 核心业务概念：
 * - State：响应式数据源，组件通过订阅自动更新
 * - Getters：计算属性，派生状态
 * - Actions：状态变更方法，可包含异步逻辑
 *
 * 线程安全性：Pinia Store 在单线程 JavaScript 环境中运行，
 * 但需注意：不要在 actions 中直接修改外部传入的对象（保持不可变性）。
 *
 * 使用示例：
 * ```ts
 * // 在组件中获取 Store
 * const appStore = useAppStore()
 *
 * // 读取状态（自动响应式）
 * console.log(appStore.appName)
 *
 * // 修改状态（通过 action）
 * appStore.updateAppName('New Name')
 * ```
 *
 * 扩展建议：
 * - 持久化：使用 pinia-plugin-persistedstate 将状态保存到 localStorage
 * - 模块化：按业务域拆分多个 Store（userStore、taskStore 等）
 */
export const useAppStore = defineStore("app", {
  /**
   * State 定义：返回初始状态对象的工厂函数。
   * 为什么用函数返回：确保每个 Store 实例有独立的状态副本，避免多实例间状态污染。
   */
  state: () => ({
    /**
     * 应用显示名称，用于页面标题和品牌展示。
     * 用途：
     * - 首页标题显示
     * - 浏览器标签页标题（配合 useHead）
     * - 邮件/通知模板中的品牌名
     * 约束：建议保持简洁，不超过 20 个字符
     */
    appName: "MoBe Life",
  }),

  /**
   * Getters 定义：计算属性，基于 state 派生数据。
   * 当前未定义，未来可扩展：
   * - isDarkMode: 是否暗黑模式
   * - currentTheme: 当前主题配置
   */
  getters: {},

  /**
   * Actions 定义：修改状态的方法。
   * 当前未定义，未来可扩展：
   * - updateAppName(name: string): 更新应用名称
   * - toggleTheme(): 切换主题
   * - initApp(): 应用初始化（加载用户配置等）
   */
  actions: {},
});
