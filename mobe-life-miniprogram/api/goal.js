/**
 * 核心职责：封装目标相关接口。
 * 所属业务模块：小程序业务层 / 目标 API。
 * 重要依赖关系或外部约束：这些方法返回的都是 `request` 已解包后的业务数据。
 */
import request from '../utils/request'

/**
 * 获取目标列表。
 *
 * @param {Object} params 查询参数
 * @param {number} params.pageNum 页码
 * @param {number} params.pageSize 每页数量
 * @param {string} [params.keyword] 关键词
 * @param {string} [params.statusCode] 状态编码
 * @returns {Promise<Object>} 分页目标列表
 */
export const getGoalList = (params) => {
  return request({
    url: '/api/goals',
    method: 'GET',
    data: params,
  })
}

/**
 * 获取目标详情。
 *
 * @param {number|string} id 目标 ID
 * @returns {Promise<Object>} 目标详情
 */
export const getGoalDetail = (id) => {
  return request({
    url: `/api/goals/${id}`,
    method: 'GET',
  })
}
export const completeGoal = (id) => {
  return request({
    url: `/api/goals/${id}/complete`,
    method: 'POST',
  })
}
export const reopenGoal = (id) => {
  return request({
    url: `/api/goals/${id}/reopen`,
    method: 'POST',
  })
}

export const reopenGoalWithNodes = (id) => {
  return request({
    url: `/api/goals/${id}/reopen-all`,
    method: 'POST',
  })
}