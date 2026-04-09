/**
 * 核心职责：封装当前用户资料查询接口。
 * 所属业务模块：小程序业务层 / 用户 API。
 */
import request from '../utils/request'

/**
 * 获取当前登录用户。
 *
 * @returns {Promise<Object>} 当前用户资料对象。
 */
export const getCurrentUser = () => {
  return request({
    url: '/api/user/current',
    method: 'GET'
  })
}
