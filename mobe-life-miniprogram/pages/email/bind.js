/**
 * 核心职责：处理绑定邮箱与更换邮箱流程。
 * 所属业务模块：小程序业务层 / 账号安全。
 * 重要依赖关系或外部约束：验证码频控由后端控制，但前端倒计时仍然必要，用于减少用户重复点击和降低无效请求。
 */
import { sendEmailCode, bindEmail, sendUnbindEmailCode, unbindEmail } from '../../api/profile'

Page({
  data: {
    hasEmail: 0,
    currentEmail: '',
    maskedCurrentEmail: '',
    email: '',
    code: '',
    countdown: 0,
    timer: null,
    sendingCode: false,
    submitting: false,

    showUnbindConfirm: false,
    unbindCode: '',
    sendingUnbindCode: false,
    unbindSubmitting: false,
    unbindCountdown: 0,
    unbindTimer: null,
  },

  onLoad(options) {
    const hasEmail = Number(options.hasEmail || 0)
    const currentEmail = decodeURIComponent(options.email || '')

    this.setData({
      hasEmail,
      currentEmail,
      maskedCurrentEmail: currentEmail ? this.maskEmail(currentEmail) : '',
    })
  },

  onUnload() {
    this.clearTimer()
    this.clearUnbindTimer()
  },
  maskEmail(email) {
    if (!email || !email.includes('@')) return email
    const [name, domain] = email.split('@')
    if (!name) return email

    if (name.length <= 2) {
      return `${name[0] || ''}***@${domain}`
    }

    return `${name.slice(0, 3)}***@${domain}`
  },
  handleUnbindCodeInput(e) {
    this.setData({
      unbindCode: (e.detail.value || '').trim(),
    })
  },
  handleEmailInput(e) {
    this.setData({
      email: (e.detail.value || '').trim(),
    })
  },

  handleCodeInput(e) {
    this.setData({
      code: (e.detail.value || '').trim(),
    })
  },

  handleCancel() {
    wx.navigateBack()
  },

  isValidEmail(email) {
    return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)
  },

  async handleSendCode() {
    const { email, countdown, sendingCode, hasEmail, currentEmail } = this.data
    if (countdown > 0 || sendingCode) return

    if (!email) {
      wx.showToast({
        title: hasEmail ? '请输入新邮箱' : '请输入邮箱',
        icon: 'none',
      })
      return
    }

    if (!this.isValidEmail(email)) {
      wx.showToast({
        title: '邮箱格式不正确',
        icon: 'none',
      })
      return
    }

    if (hasEmail && email === currentEmail) {
      wx.showToast({
        title: '新邮箱不能与当前邮箱相同',
        icon: 'none',
      })
      return
    }

    try {
      this.setData({ sendingCode: true })
      await sendEmailCode({ email })

      wx.showToast({
        title: '验证码已发送',
        icon: 'success',
      })

      this.startCountdown()
    } catch (error) {
      console.error('send email code error:', error)
      wx.showToast({
        title: error?.message || '发送失败',
        icon: 'none',
      })
    } finally {
      this.setData({ sendingCode: false })
    }
  },

  startCountdown() {
    this.clearTimer()
    this.setData({ countdown: 60 })

    const timer = setInterval(() => {
      const next = this.data.countdown - 1
      if (next <= 0) {
        this.clearTimer()
        this.setData({ countdown: 0 })
        return
      }
      this.setData({ countdown: next })
    }, 1000)

    this.setData({ timer })
  },

  clearTimer() {
    if (this.data.timer) {
      clearInterval(this.data.timer)
      this.setData({ timer: null })
    }
  },

  async handleBindEmail() {
    const { email, code, submitting, hasEmail, currentEmail } = this.data
    if (submitting) return

    if (!email) {
      wx.showToast({
        title: hasEmail ? '请输入新邮箱' : '请输入邮箱',
        icon: 'none',
      })
      return
    }

    if (!this.isValidEmail(email)) {
      wx.showToast({
        title: '邮箱格式不正确',
        icon: 'none',
      })
      return
    }

    if (hasEmail && email === currentEmail) {
      wx.showToast({
        title: '新邮箱不能与当前邮箱相同',
        icon: 'none',
      })
      return
    }

    if (!code) {
      wx.showToast({
        title: '请输入验证码',
        icon: 'none',
      })
      return
    }

    try {
      this.setData({ submitting: true })

      await bindEmail({
        email,
        code,
      })

      wx.showToast({
        title: hasEmail ? '更换成功' : '绑定成功',
        icon: 'success',
      })

      setTimeout(() => {
        wx.navigateBack()
      }, 500)
    } catch (error) {
      console.error('bind email error:', error)
      wx.showToast({
        title: error?.message || '操作失败',
        icon: 'none',
      })
    } finally {
      this.setData({ submitting: false })
    }
  },
  handleUnbindEmail() {
    const { sendingUnbindCode, unbindCountdown, showUnbindConfirm } = this.data
    console.log('handleUnbindEmail triggered', {
      sendingUnbindCode,
      unbindCountdown,
      showUnbindConfirm,
    })
  
    if (showUnbindConfirm) return
    if (sendingUnbindCode || unbindCountdown > 0) return
  
    console.log('before showModal')
  
    wx.showModal({
      title: '解绑邮箱',
      content: '将向当前绑定邮箱发送验证码，验证通过后才会解绑。',
      confirmText: '发送',
      cancelText: '取消',
      success: async (res) => {
        console.log('showModal success', res)
  
        if (!res.confirm) return
  
        try {
          this.setData({ sendingUnbindCode: true })
  
          await sendUnbindEmailCode()
  
          wx.showToast({
            title: '验证码已发送',
            icon: 'success',
          })
  
          this.setData({
            showUnbindConfirm: true,
          })
  
          this.startUnbindCountdown()
        } catch (error) {
          console.error('send unbind email code error:', error)
          wx.showToast({
            title: error?.message || '发送失败',
            icon: 'none',
          })
        } finally {
          this.setData({ sendingUnbindCode: false })
        }
      },
      fail: (err) => {
        console.error('showModal fail', err)
      },
      complete: (res) => {
        console.log('showModal complete', res)
      },
    })
  
    console.log('after showModal')
  },

  startUnbindCountdown() {
    this.clearUnbindTimer()
    this.setData({ unbindCountdown: 60 })

    const timer = setInterval(() => {
      const next = this.data.unbindCountdown - 1
      if (next <= 0) {
        this.clearUnbindTimer()
        this.setData({ unbindCountdown: 0 })
        return
      }
      this.setData({ unbindCountdown: next })
    }, 1000)

    this.setData({ unbindTimer: timer })
  },

  clearUnbindTimer() {
    if (this.data.unbindTimer) {
      clearInterval(this.data.unbindTimer)
      this.setData({ unbindTimer: null })
    }
  },

  async handleConfirmUnbindEmail() {
    const { unbindCode, unbindSubmitting } = this.data
    if (unbindSubmitting) return
  
    if (!unbindCode) {
      wx.showToast({
        title: '请输入验证码',
        icon: 'none',
      })
      return
    }
  
    try {
      this.setData({ unbindSubmitting: true })
  
      await unbindEmail({
        code: unbindCode,
      })
  
      wx.showToast({
        title: '解绑成功',
        icon: 'success',
      })
  
      setTimeout(() => {
        wx.navigateBack()
      }, 500)
    } catch (error) {
      console.error('unbind email error:', error)
      wx.showToast({
        title: error?.message || '解绑失败',
        icon: 'none',
      })
    } finally {
      this.setData({ unbindSubmitting: false })
    }
  },
  handleCancelUnbind() {
    this.setData({
      showUnbindConfirm: false,
      unbindCode: '',
    })
  },
  async handleResendUnbindCode() {
    const { sendingUnbindCode, unbindCountdown } = this.data
    if (sendingUnbindCode || unbindCountdown > 0) return
  
    try {
      this.setData({ sendingUnbindCode: true })
  
      await sendUnbindEmailCode()
  
      wx.showToast({
        title: '验证码已发送',
        icon: 'success',
      })
  
      this.startUnbindCountdown()
    } catch (error) {
      console.error('resend unbind email code error:', error)
      wx.showToast({
        title: error?.message || '发送失败',
        icon: 'none',
      })
    } finally {
      this.setData({ sendingUnbindCode: false })
    }
  },
  noop() {},
})
