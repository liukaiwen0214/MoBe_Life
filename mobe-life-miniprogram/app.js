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

const FONT_URL = 'http://119.91.216.175:8080/fonts/MoBeFont-Subset.ttf'

App({
  globalData: {
    userInfo: null,
    fontLoaded: false,
    fontLoading: false,
  },

  onLaunch() {
    this.loadGlobalFont()
  },

  /**
   * 全局加载自定义字体。
   *
   * 设计目标：
   * 1. 只在应用入口触发，不让页面层重复加载
   * 2. 通过 fontLoaded / fontLoading 双标志位避免重复请求
   * 3. 加载失败时不阻断应用，页面自动回退系统字体
   */
  loadGlobalFont() {
    if (this.globalData.fontLoaded || this.globalData.fontLoading) {
      return
    }

    this.globalData.fontLoading = true

    wx.loadFontFace({
      family: 'MoBeFont',
      source: `url("${FONT_URL}")`,
      global: true,
      success: () => {
        console.log('字体加载成功')
        this.globalData.fontLoaded = true
        this.globalData.fontLoading = false
      },
      fail: (err) => {
        console.error('字体加载失败', err)
        this.globalData.fontLoading = false
      },
      complete: () => {
        console.log('字体加载流程结束')
      },
    })
  },
})
