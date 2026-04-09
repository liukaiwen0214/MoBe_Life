/**
 * 文件级注释：
 * 核心职责：处理用户登录流程，调用微信登录 API 获取 code，再与后端交互完成身份认证。
 * 所属业务模块：小程序业务层 - 登录页面。
 * 重要依赖：
 * - wxMiniLogin：后端登录接口
 * - getCurrentUser：获取当前用户信息接口
 * - storage：本地 Token 存储工具
 * 设计约束：
 * - 页面路径：pages/login/login
 * - 登录成功后跳转到首页
 * - 防止重复点击（loading 状态控制）
 */

import { wxMiniLogin } from '../../api/auth'
import { getCurrentUser } from '../../api/user'
import storage from '../../utils/storage'

/**
 * 登录页面逻辑。
 *
 * 设计初衷：提供用户登录入口，处理微信一键登录的完整流程，
 * 包括获取微信 code、后端认证、保存 Token、获取用户信息等步骤。
 *
 * 在架构中的角色：页面控制器（Page Controller），
 * 管理登录页面的数据状态和用户交互逻辑。
 *
 * 登录流程：
 * 1. 用户点击登录按钮
 * 2. 调用 wx.login() 获取微信临时 code
 * 3. 将 code 发送给后端换取 Token
 * 4. 保存 Token 到本地存储
 * 5. 获取用户信息并保存到全局数据
 * 6. 显示成功提示，跳转到首页
 *
 * 异常处理：
 * - 微信登录失败：提示"登录失败"
 * - 后端返回错误：提示具体错误信息
 * - 网络异常：提示"网络异常"
 * - 所有异常情况都会清除可能已保存的脏 Token
 */
Page({
  /**
   * 页面初始数据。
   *
   * loading：控制登录按钮的加载状态，防止重复提交。
   * 为什么需要：wx.login 和 HTTP 请求都是异步操作，
   * 用户可能多次点击按钮，导致重复发送请求。
   */
  data: {
    loading: false,
  },

  /**
   * 处理用户登录事件。
   *
   * 触发时机：用户点击登录按钮时触发。
   *
   * 方法作用：执行完整的登录流程，包括微信登录、后端认证、数据保存和页面跳转。
   *
   * 副作用：
   * 1. 设置 loading 状态为 true（禁用按钮）
   * 2. 调用 wx.login 获取微信 code
   * 3. 调用后端登录接口换取 Token
   * 4. 保存 Token 到本地存储
   * 5. 获取用户信息并保存到全局数据
   * 6. 显示成功提示
   * 7. 跳转到首页
   * 8. 无论成功与否，最终设置 loading 为 false
   *
   * 异常场景：
   * - 未获取到 code：提示"登录失败"
   * - 后端返回数据异常：提示"登录失败"
   * - 网络错误：提示"登录失败"
   *
   * 幂等性设计：
   * - 通过 loading 状态防止重复点击
   * - 登录失败时清除已保存的 Token，避免脏数据
   */
  async handleLogin() {
    // 防重复提交：如果正在登录中，直接返回
    if (this.data.loading) return

    // 设置加载状态，禁用登录按钮
    this.setData({ loading: true })

    try {
      /**
       * 第一步：获取微信登录凭证 code。
       * wx.login 是微信提供的静默登录接口，无需用户授权。
       * 返回的 code 有效期 5 分钟，只能使用一次。
       */
      const loginRes = await new Promise((resolve, reject) => {
        wx.login({
          success: resolve,
          fail: reject,
        })
      })

      // 校验 code 是否存在（理论上一定存在，防御性编程）
      if (!loginRes.code) {
        throw new Error('未获取到微信登录code')
      }

      /**
       * 第二步：调用后端登录接口，用 code 换取 Token。
       * 后端会用 code 请求微信服务器，获取用户 openid 和 session_key。
       */
      const authRes = await wxMiniLogin({
        code: loginRes.code,
      })

      /**
       * 第三步：提取 Token 并保存。
       * 注意：这里假设后端返回结构为 { data: { token: 'xxx' } }
       * 实际结构应根据后端接口文档调整。
       */
      const token = authRes?.data?.token
      if (!token) {
        throw new Error('登录返回数据异常')
      }

      storage.setToken(token)

      /**
       * 第四步：获取用户信息并保存到全局数据。
       * 用于其他页面快速获取用户信息，无需再次请求。
       */
      const userRes = await getCurrentUser()
      getApp().globalData.userInfo = userRes.data

      // 第五步：显示登录成功提示
      wx.showToast({
        title: '登录成功',
        icon: 'success',
      })

      // 第六步：延迟跳转到首页，让用户看到成功提示
      setTimeout(() => {
        wx.reLaunch({
          url: '/pages/index/index',
        })
      }, 300)
    } catch (error) {
      // 错误处理：打印日志、清理脏数据、提示用户
      console.error('login error:', error)
      storage.removeToken()

      wx.showToast({
        title: '登录失败',
        icon: 'none',
      })
    } finally {
      // 无论成功与否，重置加载状态，允许再次点击
      this.setData({ loading: false })
    }
  },
})
