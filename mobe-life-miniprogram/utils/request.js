/**
 * 文件级注释：
 * 核心职责：封装微信小程序 HTTP 请求，统一处理基础路径、Token 注入、错误处理和响应解析。
 * 所属业务模块：小程序基础设施层 - 网络请求模块。
 * 重要依赖：
 * - wx.request：微信原生网络请求 API
 * - config.baseUrl：API 基础路径配置
 * - storage：本地 Token 存取工具
 * 设计约束：
 * - 返回 Promise，支持 async/await 语法
 * - 统一处理 HTTP 状态码和业务状态码
 * - 自动注入 Authorization Header
 */

import config from '../config/index'
import storage from './storage'

/**
 * 发起 HTTP 请求的通用方法。
 *
 * 设计初衷：解决 wx.request 回调式 API 使用繁琐的问题，
 * 提供 Promise 封装和统一的请求/响应处理逻辑。
 *
 * 在架构中的角色：基础设施层工具函数，被所有 API 模块调用，
 * 作为与后端通信的唯一通道。
 *
 * 核心能力：
 * - 自动拼接 baseUrl 和请求路径
 * - 自动注入 Token（如存在）
 * - 统一处理 HTTP 错误和业务错误
 * - 返回 Promise，支持 async/await
 *
 * 请求流程：
 * 1. 从 storage 读取 Token
 * 2. 构造完整 URL（baseUrl + path）
 * 3. 注入 Authorization Header
 * 4. 发起 wx.request
 * 5. 解析响应，根据状态码决定 resolve/reject
 *
 * 错误处理策略：
 * - HTTP 状态码 ≠ 200：reject，message 取自响应体或默认值
 * - 业务状态码 ≠ 0：reject，message 取自响应体
 * - 网络异常：reject，message 取自 err.errMsg
 *
 * 与后端响应格式约定：
 * ```json
 * {
 *   "code": 0,      // 业务状态码，0 表示成功
 *   "message": "",  // 提示信息
 *   "data": {}      // 业务数据
 * }
 * ```
 *
 * 使用示例：
 * ```js
 * import request from '../../utils/request'
 *
 * // GET 请求
 * const user = await request({ url: '/api/user/current' })
 *
 * // POST 请求
 * const result = await request({
 *   url: '/api/auth/login',
 *   method: 'POST',
 *   data: { code: 'xxx' }
 * })
 * ```
 *
 * @param options 请求配置对象
 * @param options.url 请求路径（不含 baseUrl），如 '/api/user/current'
 * @param options.method HTTP 方法，默认 'GET'
 * @param options.data 请求体数据（POST/PUT 时使用）
 * @param options.header 自定义请求头（会合并到默认头中）
 *
 * @returns Promise 解析后端 response.data，拒绝时返回 Error 对象
 */
const request = (options) => {
  /**
   * 从本地存储读取 Token，用于身份验证。
   * 为什么每次请求都读取：Token 可能在其他页面被刷新或清除，
   * 实时读取确保使用最新的 Token。
   */
  const token = storage.getToken()

  return new Promise((resolve, reject) => {
    wx.request({
      /**
       * 完整请求 URL，由基础路径和接口路径拼接。
       * 设计为拼接而非完整 URL：便于环境切换（开发/测试/生产）。
       */
      url: `${config.baseUrl}${options.url}`,

      /**
       * HTTP 方法，默认 GET。
       * 允许值：GET、POST、PUT、DELETE、HEAD、OPTIONS、TRACE、CONNECT
       */
      method: options.method || 'GET',

      /**
       * 请求体数据，仅对 POST/PUT 有效。
       * 为什么默认空对象：避免 undefined 导致的 JSON 序列化问题。
       */
      data: options.data || {},

      /**
       * 请求头配置。
       * 合并策略：默认值 + Token（如存在）+ 自定义头
       * - Content-Type: application/json：告知后端请求体为 JSON
       * - Authorization: Bearer {token}：OAuth2.0 标准 Token 格式
       */
      header: {
        'Content-Type': 'application/json',
        // 条件展开：Token 存在时注入 Authorization 头
        ...(token ? { Authorization: `Bearer ${token}` } : {}),
        // 自定义头覆盖默认值（如有冲突）
        ...(options.header || {}),
      },

      /**
       * 请求成功回调（HTTP 层面成功，statusCode 可能为 4xx/5xx）。
       *
       * @param res 响应对象
       * @param res.statusCode HTTP 状态码
       * @param res.data 响应体（已根据 Content-Type 解析）
       */
      success(res) {
        // 提取响应数据，防御性处理（某些情况下可能为 undefined）
        const response = res.data || {}
        console.log('request success =>', options.url, res.statusCode, response)

        /**
         * 第一层校验：HTTP 状态码。
         * 非 200 表示网络层或网关层错误（如 404、500、502）。
         */
        if (res.statusCode !== 200) {
          console.log('request reject by statusCode')
          // 优先使用后端返回的 message，否则使用默认提示
          reject(new Error(response.message || '请求失败'))
          return
        }

        /**
         * 第二层校验：业务状态码。
         * 与后端约定：code === 0 表示业务成功，非 0 表示具体业务错误。
         * 注意：此处使用 !== 0 而非 === 200，因为后端使用 0 表示成功。
         */
        if (response.code !== 0) {
          console.log('request reject by biz code')
          reject(new Error(response.message || '请求失败'))
          return
        }

        // 校验通过，返回业务数据（response.data）
        console.log('request resolve success')
        resolve(response.data)
      },

      /**
       * 请求失败回调（网络层失败，如断网、DNS 解析失败、超时）。
       *
       * @param err 错误对象
       * @param err.errMsg 错误描述，如 "request:fail timeout"
       */
      fail(err) {
        console.log('request fail =>', err)
        // 统一转换为 Error 对象，保持与 success 中 reject 的一致性
        reject(new Error(err.errMsg || '网络异常'))
      },
    })
  })
}

export default request
