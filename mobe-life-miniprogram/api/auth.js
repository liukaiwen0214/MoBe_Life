/**
 * 核心职责：封装认证相关接口。
 * 所属业务模块：小程序业务层 / 认证 API。
 * 重要依赖关系或外部约束：这些方法返回的都是 `request` 已解包后的业务数据。
 */
import request from '../utils/request'

/**
 * 微信小程序登录。
 *
 * @param {Object} data 登录参数，至少包含微信 `code`。
 * @returns {Promise<Object>} 登录后的用户信息和 token。
 */
export const wxMiniLogin = (data) => {
  return request({
    url: '/api/auth/wx-mini-login',
    method: 'POST',
    data,
  })
}

/**
 * 登出。
 *
 * @returns {Promise<boolean>} 成功时返回 `true`。
 */
export const logout = () => {
  return request({
    url: '/api/auth/logout',
    method: 'POST',
  })
}
/**
 * 注销账号
 * @returns {Promise<boolean>} 成功时返回 `true`。
 */
export const cancelAccount = () => {
  return request({
    url: '/api/auth/cancel-account',
    method: 'POST',
  })
}
