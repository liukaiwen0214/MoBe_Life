/**
 * 文件级注释：
 * 核心职责：微信小程序应用入口，管理全局状态和生命周期，处理应用级初始化逻辑。
 * 所属业务模块：小程序基础设施层 - 应用实例。
 * 重要依赖：
 * - 微信原生 API：wx.loadFontFace、wx.getSystemInfo 等
 * - 全局数据对象 globalData：跨页面共享状态
 * 设计约束：
 * - App() 必须在 app.js 中调用且只能调用一次
 * - onLaunch 在小程序初始化完成时触发（全局只触发一次）
 */

App({
  globalData: {
    userInfo: null,
    fontLoaded: false,
    fontLoading: false,
  },
})
