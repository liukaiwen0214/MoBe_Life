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

/**
 * 应用实例配置对象。
 *
 * 设计初衷：遵循微信小程序框架规范，通过 App() 注册小程序，
 * 接收包含生命周期回调和全局配置的对象参数。
 *
 * 在架构中的角色：小程序根实例，作为所有页面的容器，
 * 管理应用生命周期和全局状态。
 *
 * 生命周期顺序：
 * 1. onLaunch：小程序初始化完成（冷启动）
 * 2. onShow：小程序启动或从后台进入前台
 * 3. onHide：小程序从前台进入后台
 * 4. onError：脚本错误或 API 调用失败
 *
 * 全局数据设计：
 * - userInfo：当前登录用户信息，登录成功后写入，供各页面读取
 * - fontLoaded：自定义字体加载状态标志，避免页面重复加载字体
 */
App({
  /**
   * 全局数据对象，可在任何页面通过 getApp().globalData 访问。
   *
   * 为什么不用 Storage：
   * - globalData 是内存存储，读写更快
   * - 适合存储临时状态（如当前用户信息）
   * - 注意：小程序重启后数据会丢失，持久化数据应使用 wx.setStorage
   */
  globalData: {
    /**
     * 当前登录用户信息。
     * 结构：{ id, nickname, avatar, phone, email, ... }
     * 生命周期：登录成功后写入，退出登录时置为 null
     */
    userInfo: null,

    /**
     * 自定义字体加载状态标志。
     * 为什么需要：wx.loadFontFace 是异步操作，多个页面可能同时触发加载，
     * 通过全局标志位避免重复加载，节省网络请求和内存。
     */
    fontLoaded: false,
  },

  /**
   * 应用初始化生命周期回调。
   *
   * 触发时机：小程序初始化完成时（全局只触发一次）。
   * 冷启动：用户首次打开或小程序被销毁后再次打开。
   * 热启动：小程序从后台切换到前台不会触发。
   *
   * 方法作用：执行应用级的一次性初始化操作，如：
   * - 加载全局资源（字体、配置）
   * - 初始化第三方 SDK
   * - 检查更新
   *
   * 副作用：
   * 1. 调用 wx.loadFontFace 加载霞鹜文楷字体
   * 2. 字体加载成功后设置 fontLoaded = true
   *
   * 字体加载配置说明：
   * - family: 'MoBeFont' - 字体族名称，在 CSS 中通过 font-family: MoBeFont 使用
   * - source: 字体文件 URL，开发环境使用本地服务器，生产环境应使用 CDN
   * - global: true - 全局生效，所有页面可用
   *
   * 异常处理：
   * - 字体加载失败仅打印日志，不影响应用启动
   * - 页面应做好字体加载失败的降级显示（使用系统默认字体）
   */
  onLaunch() {
    wx.loadFontFace({
      family: 'MoBeFont',
      // 开发环境使用本地服务器，生产环境应替换为 CDN 地址
      source: 'url("http://127.0.0.1:8080/fonts/LXGWWenKai-Light.ttf")',
      global: true, // 全局加载，所有页面共享
      success: () => {
        console.log('字体加载成功')
        this.globalData.fontLoaded = true
      },
      fail: (err) => {
        // 字体加载失败不应阻断应用启动，仅记录日志
        console.error('字体加载失败', err)
        // 页面应使用系统默认字体作为 fallback
      },
    })
  },
})
