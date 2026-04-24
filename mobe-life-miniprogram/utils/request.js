/**
 * 核心职责：封装小程序侧所有 HTTP 请求，统一处理地址拼接、token 注入和业务码解析。
 * 所属业务模块：小程序基础设施 / 网络请求。
 * 重要依赖关系或外部约束：该工具最终 `resolve` 的是后端 `Result.data`，调用方不应再额外取一层 `.data`。
 */
import config from '../config/index'
import storage from './storage'

/**
 * 发起 HTTP 请求。
 *
 * @param options 请求配置对象，不允许为空。
 * @returns {Promise<any>} 成功时直接返回后端 `data` 字段。
 */
const request = (options) => {
  const token = storage.getToken()

  return new Promise((resolve, reject) => {
    wx.request({
      url: `${config.baseUrl}${options.url}`,
      method: options.method || 'GET',
      data: options.data || {},
      header: {
        'Content-Type': 'application/json',
        ...(token ? { Authorization: `Bearer ${token}` } : {}),
        ...(options.header || {}),
      },
      success(res) {
        const response = res.data || {}

        // HTTP 层失败通常意味着网关、路由或服务本身异常，此时直接按错误分支处理。
        if (res.statusCode !== 200) {
          reject(new Error(response.message || '请求失败'))
          return
        }

        // 业务码非 0 说明后端已经明确判定失败，调用方应统一走 catch 分支而不是继续判空使用数据。
        if (response.code !== 0) {
          reject(new Error(response.message || '请求失败'))
          return
        }

        // 这里直接把业务层真正关心的 `data` 解包出来，减少页面代码到处写 `res.data.data`。
        resolve(response.data)
      },
      fail(err) {
        reject(new Error(err.errMsg || '网络异常'))
      },
    })
  })
}

export default request
