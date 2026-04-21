/**
 * 核心职责：封装节点相关接口。
 * 所属业务模块：小程序业务层 / 节点 API。
 * 重要依赖关系或外部约束：这些方法返回的都是 `request` 已解包后的业务数据。
 */
import request from '../utils/request'

/**
 * 获取节点列表。
 *
 * @param {Object} params 查询参数
 * @param {number} params.pageNum 页码
 * @param {number} params.pageSize 每页数量
 * @param {string} [params.keyword] 关键词
 * @param {string} [params.ownerType] 所属类型
 * @returns {Promise<Object>} 分页节点列表
 */
export const getNodeList = (params) => {
  return request({
    url: '/api/nodes',
    method: 'GET',
    data: params,
  })
}

/**
 * 获取节点详情。
 *
 * @param {number|string} id 节点 ID
 * @returns {Promise<Object>} 节点详情
 */
export const getNodeDetail = (id) => {
  return request({
    url: `/api/nodes/${id}`,
    method: 'GET',
  })
}
export const completeNode = (id) => {
  return request({
    url: `/api/nodes/${id}/complete`,
    method: 'POST',
  })
}