import { getCurrentUser } from '../../api/user'
import { logout } from '../../api/auth'
import { updateUserProfile } from '../../api/profile'
import storage from '../../utils/storage'
import config from '../../config/index'

Page({
  data: {
    loading: true,
    profile: {
      avatar: '',
      nickname: '微信用户',
      signature: '还没有填写个性签名',
      genderText: '未设置',
      birthdayText: '未设置',
      phoneText: '未维护手机号',
      emailText: '未维护邮箱',
      genderOptions: ['未设置', '男', '女'],
      genderValueMap: [0, 1, 2],
      birthdayPickerValue: '2000-01-01',
    },
  },

  onShow() {
    this.loadProfile()
  },

  async loadProfile() {
    this.setData({ loading: true })

    try {
      
      const user = await getCurrentUser()

      this.setData({
        profile: {
          avatar: user.avatar || '/assets/images/default-avatar.png',
          nickname: user.nickname || '微信用户',
          signature: '还没有填写个性签名',
          genderText: this.formatGender(user.gender),
          birthdayText: user.birthday || '未设置',
          phoneText: user.phone || '未维护手机号',
          emailText: user.email || '未维护邮箱',
          hasPassword: !!user.hasPassword,
          hasPhone: !!user.hasPhone,
          hasEmail: !!user.hasEmail,
        },
      })
    } catch (error) {
      console.error('load profile error:', error)
      wx.showToast({
        title: '获取资料失败',
        icon: 'none',
      })
    } finally {
      this.setData({ loading: false })
    }
  },

  formatGender(gender) {
    if (gender === 1) return '男'
    if (gender === 2) return '女'
    return '未设置'
  },

  handleGender() {
    wx.showToast({ title: '后续开放', icon: 'none' })
  },

  handleBirthday() {
    wx.showToast({ title: '后续开放', icon: 'none' })
  },

  handleEmail() {
    const rawEmail = this.data.profile.emailText || ''
    const hasEmail = rawEmail && rawEmail !== '未维护邮箱' && rawEmail !== '未绑定邮箱'
  
    wx.navigateTo({
      url: `/pages/email/bind?hasEmail=${hasEmail ? 1 : 0}&email=${encodeURIComponent(hasEmail ? rawEmail : '')}`,
    })
  },

  handlePassword() {
    const { hasPhone, hasEmail, hasPassword } = this.data.profile
  
    if (!hasPhone && !hasEmail) {
      wx.showToast({
        title: '请先绑定手机号或邮箱',
        icon: 'none',
      })
      return
    }
  
    wx.navigateTo({
      url: `/pages/password/index?hasPassword=${hasPassword ? 1 : 0}`,
    })
  },

  async handleLogout() {
    try {
      await logout()
    } catch (error) {
      console.error('logout error:', error)
    }

    storage.removeToken()
    getApp().globalData.userInfo = null

    wx.reLaunch({
      url: '/pages/login/login',
    })
  },

  handleCancelAccount() {
    wx.showToast({
      title: '后续开放',
      icon: 'none',
    })
  },
  async handleChooseAvatar(e) {
    try {
      const tempAvatarPath = e.detail.avatarUrl
      if (!tempAvatarPath) {
        throw new Error('未获取到微信头像')
      }

      wx.showLoading({
        title: '上传中',
        mask: true,
      })

      const avatarUrl = await this.uploadAvatarFile(tempAvatarPath)

      await updateUserProfile({
        avatar: avatarUrl,
      })

      await this.loadProfile()

      wx.hideLoading()
      wx.showToast({
        title: '头像已更新',
        icon: 'success',
      })
    } catch (error) {
      wx.hideLoading()
      console.error('choose avatar error:', error)
      wx.showToast({
        title: '更新失败',
        icon: 'none',
      })
    }
  },

  uploadAvatarFile(filePath) {
    const token = storage.getToken()

    return new Promise((resolve, reject) => {
      wx.uploadFile({
        url: `${config.baseUrl}/api/file/upload/avatar`,
        filePath,
        name: 'file',
        header: {
          Authorization: `Bearer ${token}`,
        },
        success: (res) => {
          try {
            const data = JSON.parse(res.data || '{}')
            if (res.statusCode !== 200 || data.code !== 0 || !data.data?.url) {
              reject(data)
              return
            }
            resolve(data.data.url)
          } catch (err) {
            reject(err)
          }
        },
        fail: reject,
      })
    })
  },
  async handleGender() {
    wx.showActionSheet({
      itemList: ['未设置', '男', '女'],
      success: async (res) => {
        try {
          const genderMap = [0, 1, 2]
          const gender = genderMap[res.tapIndex]
  
          wx.showLoading({
            title: '保存中',
            mask: true,
          })
  
          await updateUserProfile({
            gender,
          })
  
          await this.loadProfile()
  
          wx.hideLoading()
          wx.showToast({
            title: '已更新',
            icon: 'success',
          })
        } catch (error) {
          wx.hideLoading()
          console.error('update gender error:', error)
          wx.showToast({
            title: '更新失败',
            icon: 'none',
          })
        }
      },
    })
  },
  
  handleBirthday() {
    this.setData({
      birthdayPickerValue:
        this.data.profile.birthdayText && this.data.profile.birthdayText !== '未设置'
          ? this.data.profile.birthdayText
          : '2000-01-01',
    })
  },
  
  async handleBirthdayChange(e) {
    const birthday = e.detail.value
  
    try {
      wx.showLoading({
        title: '保存中',
        mask: true,
      })
  
      await updateUserProfile({
        birthday,
      })
  
      await this.loadProfile()
  
      wx.hideLoading()
      wx.showToast({
        title: '已更新',
        icon: 'success',
      })
    } catch (error) {
      wx.hideLoading()
      console.error('update birthday error:', error)
      wx.showToast({
        title: '更新失败',
        icon: 'none',
      })
    }
  },
  async handleGetPhoneNumber(e) {
    console.log('getPhoneNumber detail:', e.detail)
  
    try {
      const { code, errMsg } = e.detail || {}
  
      if (!code) {
        wx.showToast({
          title: errMsg && errMsg.includes('deny') ? '你已取消授权' : '未获取到微信手机号授权',
          icon: 'none',
        })
        return
      }
  
      wx.showLoading({
        title: '绑定中',
        mask: true,
      })
  
      await bindPhone({ code })
      await this.loadProfile()
  
      wx.hideLoading()
      wx.showToast({
        title: '手机号已绑定',
        icon: 'success',
      })
    } catch (error) {
      wx.hideLoading()
      console.error('bind phone error:', error)
      wx.showToast({
        title: '绑定失败',
        icon: 'none',
      })
    }
  },
})