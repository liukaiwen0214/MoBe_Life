/**
 * 核心职责：承载个人中心资料展示与账号安全入口。
 * 所属业务模块：小程序业务层 / 个人中心。
 * 重要依赖关系或外部约束：页面状态高度依赖当前用户资料，因此每次展示时都会主动刷新而不是只吃全局缓存。
 */
import { getCurrentUser } from '../../api/user'
import { logout, cancelAccount } from '../../api/auth'
import { updateUserProfile, bindPhone } from '../../api/profile'
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

  /**
   * 拉取当前用户资料。
   * 这里把后端原始字段转换成页面直接可消费的展示文案，是为了避免 WXML 里堆满条件判断。
   */
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

  handleEmail() {
    const rawEmail = this.data.profile.emailText || ''
    const hasEmail =
      rawEmail && rawEmail !== '未维护邮箱' && rawEmail !== '未绑定邮箱'

    wx.navigateTo({
      url: `/pages/email/bind?hasEmail=${hasEmail ? 1 : 0}&email=${encodeURIComponent(hasEmail ? rawEmail : '')}`,
    })
  },

  /**
   * 只有账号已经绑定手机号或邮箱时才允许设置本地密码，
   * 这是为了保住最基本的账号找回路径。
   */
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
    wx.showModal({
      title: '退出登录',
      content: '确认退出当前账号吗？',
      confirmText: '退出',
      cancelText: '取消',
      success: async (res) => {
        if (!res.confirm) return

        try {
          wx.showLoading({
            title: '退出中',
            mask: true,
          })

          try {
            await logout()
          } catch (error) {
            console.error('logout request error:', error)
          }

          storage.removeToken()

          const app = getApp()
          if (app && app.globalData) {
            app.globalData.userInfo = null
          }

          wx.hideLoading()
          wx.reLaunch({
            url: '/pages/login/login',
          })
        } catch (error) {
          wx.hideLoading()
          console.error('handle logout error:', error)
          wx.showToast({
            title: '退出失败',
            icon: 'none',
          })
        }
      },
    })
  },
  handleCancelAccount() {
    wx.showModal({
      title: '注销账号',
      content: '注销后账号将无法恢复，是否继续？',
      confirmText: '确认注销',
      confirmColor: '#d84f4f',
      cancelText: '取消',
      success: async (res) => {
        if (!res.confirm) return

        try {
          wx.showLoading({
            title: '处理中',
            mask: true,
          })

          await cancelAccount()

          storage.removeToken()

          const app = getApp()
          if (app && app.globalData) {
            app.globalData.userInfo = null
          }

          wx.hideLoading()
          wx.showToast({
            title: '账号已注销',
            icon: 'success',
          })

          setTimeout(() => {
            wx.reLaunch({
              url: '/pages/login/login',
            })
          }, 500)
        } catch (error) {
          wx.hideLoading()
          console.error('cancel account error:', error)
          wx.showToast({
            title: error?.message || '注销失败',
            icon: 'none',
          })
        }
      },
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

  /**
   * 头像上传单独走 `wx.uploadFile`，因为它提交的是二进制文件流，不适合复用普通 JSON request 封装。
   */
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

  handleGender() {
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

  /**
   * 这里只负责把 picker 初始化到当前生日值，真正提交放在 `handleBirthdayChange`，
   * 是为了让用户有明确确认动作，而不是一点击入口就立刻写库。
   */
  handleBirthday() {
    this.setData({
      birthdayPickerValue:
        this.data.profile.birthdayText &&
        this.data.profile.birthdayText !== '未设置'
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
    try {
      const { code, errMsg } = e.detail || {}

      if (!code) {
        wx.showToast({
          title:
            errMsg && errMsg.includes('deny')
              ? '你已取消授权'
              : '未获取到微信手机号授权',
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
        title: error?.message || '绑定失败',
        icon: 'none',
      })
    }
  },
})
