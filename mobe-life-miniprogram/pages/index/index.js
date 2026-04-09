/**
 * 核心职责：作为小程序首页入口，负责校验本地登录态并拉取当前用户资料。
 * 所属业务模块：小程序业务层 / 首页。
 * 重要依赖关系或外部约束：首页依赖 token 存在且后端可正常返回当前用户，否则会强制回到登录页。
 */
import storage from '../../utils/storage'
import { getCurrentUser } from '../../api/user'

Page({
  data: {
    loading: true,
    userInfo: null,
  },

  /**
   * 每次回到首页都重新校验一次登录态，避免用户在“我的”页退出后首页仍显示旧缓存。
   */
  async onShow() {
    const token = storage.getToken()

    if (!token) {
      wx.redirectTo({
        url: '/pages/login/login',
      })
      return
    }

    try {
      const user = await getCurrentUser()

      getApp().globalData.userInfo = user

      this.setData({
        loading: false,
        userInfo: user,
      })
    } catch (error) {
      console.error('token check failed:', error)
      storage.removeToken()

      wx.redirectTo({
        url: '/pages/login/login',
      })
    }
  },
})
