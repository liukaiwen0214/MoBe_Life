/**
 * 文件级注释：
 * 核心职责：封装微信小程序本地存储操作，提供 Token 的存取删方法。
 * 所属业务模块：小程序基础设施层 - 本地存储模块。
 * 重要依赖：
 * - wx.setStorageSync：同步写入本地存储
 * - wx.getStorageSync：同步读取本地存储
 * - wx.removeStorageSync：同步删除本地存储
 * 设计约束：
 * - 使用同步 API（Sync），简化调用方代码（无需 await）
 * - 单 key 设计（MOBE_TOKEN），专注于 Token 管理
 * - 存储容量限制：单个 key 最大 1MB，总容量 10MB
 */

/**
 * Token 存储键名，使用大写+下划线命名表示常量。
 * 为什么加 MOBE_ 前缀：避免与其他业务或第三方 SDK 的 key 冲突。
 * 安全性说明：
 * - 小程序存储在设备本地，非加密存储
 * - Token 有有效期，泄露风险可控
 * - 敏感数据（如密码）不应使用此工具存储
 */
const TOKEN_KEY = 'MOBE_TOKEN'

/**
 * 保存 Token 到本地存储。
 *
 * 方法作用：将用户登录获取的 Token 持久化存储，供后续请求使用。
 *
 * @param token JWT Token 字符串
 *              - 格式：eyJhbGciOiJIUzI1NiJ9.xxx.xxx
 *              - 约束：非空字符串，由后端登录接口返回
 *
 * 副作用：
 * 1. 调用 wx.setStorageSync 写入本地存储
 * 2. 数据持久保存，小程序关闭后仍存在
 * 3. 用户卸载小程序后数据清除
 *
 * 异常说明：
 * - 存储空间不足：wx.setStorageSync 抛出异常（极少见，Token 通常 < 1KB）
 * - 参数类型错误：JavaScript 会自动转换为字符串存储
 */
const setToken = (token) => {
  wx.setStorageSync(TOKEN_KEY, token)
}

/**
 * 从本地存储读取 Token。
 *
 * 方法作用：获取已保存的 Token，用于请求身份验证。
 *
 * @returns {string|null} Token 字符串，未保存时返回空字符串
 *         - 注意：wx.getStorageSync 在 key 不存在时返回空字符串而非 null
 *         - 调用方应使用 if (token) 判断是否存在
 *
 * 使用场景：
 * - 每次发起 HTTP 请求前读取，注入 Authorization Header
 * - 应用启动时读取，判断用户是否已登录
 *
 * 性能说明：同步读取，耗时约 1-10ms，适合在请求前调用。
 */
const getToken = () => {
  return wx.getStorageSync(TOKEN_KEY)
}

/**
 * 从本地存储删除 Token。
 *
 * 方法作用：清除已保存的 Token，实现退出登录功能。
 *
 * 副作用：
 * 1. 调用 wx.removeStorageSync 删除指定 key
 * 2. 后续请求将无法携带 Token，后端返回 401
 *
 * 使用场景：
 * - 用户点击"退出登录"
 * - Token 过期或无效，需要重新登录
 * - 登录失败时清理脏数据
 */
const removeToken = () => {
  wx.removeStorageSync(TOKEN_KEY)
}

/**
 * 存储工具对象，导出所有方法。
 * 设计为对象形式，便于未来扩展更多存储方法（如 setUserInfo、getUserInfo 等）。
 */
export default {
  setToken,
  getToken,
  removeToken
}
