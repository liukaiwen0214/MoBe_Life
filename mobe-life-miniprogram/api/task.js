/**
 * 核心职责：封装待办相关接口。
 * 所属业务模块：小程序业务层 / 待办 API。
 * 重要依赖关系或外部约束：这些方法返回的都是 `request` 已解包后的业务数据。
 */
import request from '../utils/request'

/**
 * 获取待办列表。
 *
 * @param {Object} params 查询参数
 * @param {number} params.pageNum 页码
 * @param {number} params.pageSize 每页数量
 * @param {string} [params.keyword] 关键词
 * @param {string} [params.statusCode] 状态编码
 * @param {string} [params.directOwnerType] 归属类型
 * @returns {Promise<Object>} 分页待办列表
 */
export const getTaskList = (params) => {
  return request({
    url: '/api/tasks',
    method: 'GET',
    data: params,
  })
}

/**
 * 获取待办详情。
 *
 * @param {number|string} id 待办 ID
 * @returns {Promise<Object>} 待办详情
 */
export const getTaskDetail = (id) => {
  return request({
    url: `/api/tasks/${id}`,
    method: 'GET',
  })
}
export const createTask = (data) => {
  return request({
    url: '/api/tasks',
    method: 'POST',
    data,
  })
}
export const moveTaskToNextStatus = (id, data) => {
  return request({
    url: `/api/tasks/${id}/next-status`,
    method: 'POST',
    data,
  })
}
export const updateTask = (id, data) => {
  return request({
    url: `/api/tasks/${id}`,
    method: 'PUT',
    data,
  })
}
export const deleteTask = (id) => {
  return request({
    url: `/api/tasks/${id}`,
    method: 'DELETE',
  })
}
export const getTaskFlow = (id) => {
  return request({
    url: `/api/tasks/${id}/flow`,
    method: 'GET',
  })
}

export const releaseTaskToStatus = (id, data) => {
  return request({
    url: `/api/tasks/${id}/release`,
    method: 'POST',
    data,
  })
}