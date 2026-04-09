/**
 * 核心职责：处理首次设密和已有密码后的改密流程。
 * 所属业务模块：小程序业务层 / 账号安全。
 * 重要依赖关系或外部约束：`hasPassword` 由上一页传入，决定当前提交应该调用“设密”还是“改密”接口。
 */
import { setPassword, changePassword } from '../../api/profile'

Page({
  data: {
    hasPassword: 0,
    oldPassword: '',
    newPassword: '',
    confirmPassword: '',
    submitting: false,
  },

  onLoad(options) {
    this.setData({
      hasPassword: Number(options.hasPassword || 0),
    })
  },

  handleOldPasswordInput(e) {
    this.setData({
      oldPassword: (e.detail.value || '').trim(),
    })
  },

  handleNewPasswordInput(e) {
    this.setData({
      newPassword: (e.detail.value || '').trim(),
    })
  },

  handleConfirmPasswordInput(e) {
    this.setData({
      confirmPassword: (e.detail.value || '').trim(),
    })
  },

  async handleSubmit() {
    const { hasPassword, oldPassword, newPassword, confirmPassword, submitting } = this.data
    if (submitting) return

    if (hasPassword && !oldPassword) {
      wx.showToast({
        title: '请输入原密码',
        icon: 'none',
      })
      return
    }

    if (!newPassword) {
      wx.showToast({
        title: '请输入新密码',
        icon: 'none',
      })
      return
    }

    if (newPassword.length < 6) {
      wx.showToast({
        title: '密码不能少于6位',
        icon: 'none',
      })
      return
    }

    if (!confirmPassword) {
      wx.showToast({
        title: '请输入确认密码',
        icon: 'none',
      })
      return
    }

    if (newPassword !== confirmPassword) {
      wx.showToast({
        title: '两次输入密码不一致',
        icon: 'none',
      })
      return
    }

    try {
      this.setData({ submitting: true })

      if (hasPassword) {
        await changePassword({
          oldPassword,
          newPassword,
          confirmPassword,
        })
      } else {
        await setPassword({
          newPassword,
          confirmPassword,
        })
      }

      wx.showToast({
        title: hasPassword ? '修改成功' : '设置成功',
        icon: 'success',
      })

      setTimeout(() => {
        wx.navigateBack()
      }, 500)
    } catch (error) {
      console.error('password submit error:', error)
      wx.showToast({
        title: error?.message || '操作失败',
        icon: 'none',
      })
    } finally {
      this.setData({ submitting: false })
    }
  },
})
