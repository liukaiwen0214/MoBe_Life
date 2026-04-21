/**
 * 核心职责：封装项目相关接口。
 * 所属业务模块：小程序业务层 / 项目 API。
 * 重要依赖关系或外部约束：这些方法返回的都是 `request` 已解包后的业务数据。
 */
import request from '../utils/request'

/**
 * 获取项目列表。
 *
 * @param {Object} params 查询参数
 * @param {number} params.pageNum 页码
 * @param {number} params.pageSize 每页数量
 * @param {string} [params.keyword] 关键词
 * @param {string} [params.statusCode] 状态编码
 * @returns {Promise<Object>} 分页项目列表
 */
export const getProjectList = (params) => {
  return request({
    url: '/api/projects',
    method: 'GET',
    data: params,
  })
}

/**
 * 获取项目详情。
 *
 * @param {number|string} id 项目 ID
 * @returns {Promise<Object>} 项目详情
 */
export const getProjectDetail = (id) => {
  return request({
    url: `/api/projects/${id}`,
    method: 'GET',
  })
}

export const completeProject = (id) => {
  return request({
    url: `/api/projects/${id}/complete`,
    method: 'POST',
  })
}
export const reopenProject = (id) => {
  return request({
    url: `/api/projects/${id}/reopen`,
    method: 'POST',
  })
}

export const reopenProjectWithNodes = (id) => {
  return request({
    url: `/api/projects/${id}/reopen-all`,
    method: 'POST',
  })
}