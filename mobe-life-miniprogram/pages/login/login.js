/**
 * 核心职责：完成小程序登录闭环，包括获取微信 code、向后端换取 token，并初始化当前用户缓存。
 * 所属业务模块：小程序业务层 / 登录页面。
 * 重要依赖关系或外部约束：这里依赖 `request` 已把响应解包为业务数据，因此不能再按 `res.data` 取值。
 */
import { wxMiniLogin } from '../../api/auth'
import { getCurrentUser } from '../../api/user'
import storage from '../../utils/storage'

Page({
  data: {
    loading: false,
  },

  /**
   * 处理登录。
   * 通过 `loading` 锁定重复点击，是为了防止短时间内把同一个微信 code 重复提交给后端导致登录失败。
   */
  async handleLogin() {
    if (this.data.loading) return

    this.setData({ loading: true })

    try {
      const loginRes = await new Promise((resolve, reject) => {
        wx.login({
          success: resolve,
          fail: reject,
        })
      })

      if (!loginRes.code) {
        throw new Error('未获取到微信登录code')
      }

      const authRes = await wxMiniLogin({
        code: loginRes.code,
      })

      const token = authRes?.token
      if (!token) {
        throw new Error('登录返回数据异常')
      }

      storage.setToken(token)

      const user = await getCurrentUser()
      getApp().globalData.userInfo = user

      wx.showToast({
        title: '登录成功',
        icon: 'success',
      })

      setTimeout(() => {
        wx.reLaunch({
          url: '/pages/index/index',
        })
      }, 300)
    } catch (error) {
      console.error('login error:', error)
      storage.removeToken()

      wx.showToast({
        title: error?.message || '登录失败',
        icon: 'none',
      })
    } finally {
      this.setData({ loading: false })
    }
  },
})
