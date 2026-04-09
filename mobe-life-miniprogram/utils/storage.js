/**
 * 核心职责：封装登录 token 的本地存取。
 * 所属业务模块：小程序基础设施 / 本地存储。
 * 重要依赖关系或外部约束：这里存的是登录态，不应扩展成任意业务数据杂物箱。
 */
const TOKEN_KEY = 'MOBE_TOKEN'

/**
 * 保存 token。
 *
 * @param token 登录 token，允许为任意可序列化字符串。
 */
const setToken = (token) => {
  wx.setStorageSync(TOKEN_KEY, token)
}

/**
 * 读取 token。
 *
 * @returns {string} 已保存 token；不存在时返回空字符串。
 */
const getToken = () => {
  return wx.getStorageSync(TOKEN_KEY)
}

/**
 * 清理 token。
 * 退出登录或 token 失效时必须调用，避免小程序误以为仍处于登录态。
 */
const removeToken = () => {
  wx.removeStorageSync(TOKEN_KEY)
}

export default {
  setToken,
  getToken,
  removeToken
}
