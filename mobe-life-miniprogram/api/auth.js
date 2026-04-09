/**
 * 文件级注释：
 * 核心职责：封装与认证相关的 API 调用，包括微信小程序登录和退出登录。
 * 所属业务模块：小程序业务层 - 认证 API 模块。
 * 重要依赖：
 * - request：统一封装的 HTTP 请求工具
 * 设计约束：
 * - 每个接口对应一个导出函数，函数名与后端接口语义一致
 * - 返回 Promise，支持 async/await
 * - 参数和返回值类型与后端接口文档保持一致
 */

import request from '../utils/request'

/**
 * 微信小程序登录接口。
 *
 * 业务场景：用户首次打开小程序或 Token 过期时，调用微信登录获取 code，
 * 再将 code 发送给后端换取用户身份凭证（Token）。
 *
 * 调用流程：
 * 1. 前端调用 wx.login() 获取微信临时 code（有效期 5 分钟）
 * 2. 将 code 传入本函数，发送给后端 /api/auth/wx-mini-login
 * 3. 后端用 code 换取微信 openid，查询或创建用户，返回 Token
 * 4. 前端保存 Token，后续请求携带 Token 进行身份验证
 *
 * @param {Object} data 登录参数
 * @param {string} data.code 微信登录凭证，通过 wx.login() 获取
 *                           - 有效期：5 分钟
 *                           - 使用次数：一次性，不可重复使用
 *
 * @returns {Promise<Object>} 登录结果
 *          - code: 0 表示成功
 *          - data: { token: 'xxx', userInfo: {...} }
 *          - message: 提示信息
 *
 * 异常处理：
 * - 网络错误：Promise reject，Error.message = "网络异常"
 * - 业务错误：Promise reject，Error.message = 后端返回的错误信息
 *
 * 使用示例：
 * ```js
 * // 在登录页面调用
 * const loginRes = await wx.login()
 * const authRes = await wxMiniLogin({ code: loginRes.code })
 * storage.setToken(authRes.data.token)
 * ```
 */
export const wxMiniLogin = (data) => {
  return request({
    url: '/api/auth/wx-mini-login',
    method: 'POST',
    data
  })
}

/**
 * 用户退出登录接口。
 *
 * 业务场景：用户主动退出登录，清理服务端会话状态。
 *
 * 调用流程：
 * 1. 前端携带当前 Token 调用本接口
 * 2. 后端将 Token 加入黑名单（如实现 Token 吊销机制）
 * 3. 前端清除本地存储的 Token 和用户信息
 * 4. 跳转到登录页面
 *
 * @returns {Promise<Object>} 退出结果
 *          - code: 0 表示成功
 *          - data: true
 *
 * 注意：
 * - 本接口需要携带有效 Token（通过 request 自动注入）
 * - 即使后端返回失败，前端也应清除本地登录状态
 *
 * 使用示例：
 * ```js
 * // 在"我的"页面调用
 * await logout()
 * storage.removeToken()
 * getApp().globalData.userInfo = null
 * wx.reLaunch({ url: '/pages/login/login' })
 * ```
 */
export const logout = () => {
  return request({
    url: '/api/auth/logout',
    method: 'POST'
  })
}
