import storage from '../../utils/storage'
import { getCurrentUser } from '../../api/user'

Page({
  data: {
    loading: true,
    userInfo: null,
  },

  async onShow() {
    const token = storage.getToken()

    if (!token) {
      wx.redirectTo({
        url: '/pages/login/login',
      })
      return
    }

    try {
      const res = await getCurrentUser()

      getApp().globalData.userInfo = res.data

      this.setData({
        loading: false,
        userInfo: res.data,
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