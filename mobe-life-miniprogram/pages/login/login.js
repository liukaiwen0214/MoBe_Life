import { wxMiniLogin } from '../../api/auth'
import { getCurrentUser } from '../../api/user'
import storage from '../../utils/storage'

Page({
  data: {
    loading: false,
  },

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

      const token = authRes?.data?.token
      if (!token) {
        throw new Error('登录返回数据异常')
      }

      storage.setToken(token)

      const userRes = await getCurrentUser()
      getApp().globalData.userInfo = userRes.data

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
        title: '登录失败',
        icon: 'none',
      })
    } finally {
      this.setData({ loading: false })
    }
  },
})