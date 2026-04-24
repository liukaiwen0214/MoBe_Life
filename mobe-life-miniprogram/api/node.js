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

/**
 * 赋予节点完成态。
 *
 * @param {number|string} id 节点 ID
 * @returns {Promise<boolean>}
 */
export const completeNode = (id) => {
  return request({
    url: `/api/nodes/${id}/complete`,
    method: 'POST',
  })
}

/**
 * 新增节点。
 *
 * @param {Object} data
 * @param {string} data.ownerType 所属类型：PROJECT/GOAL
 * @param {number|string} data.ownerId 所属对象 ID
 * @param {string} data.title 节点名称
 * @param {string} [data.content] 节点说明
 * @param {string} [data.remark] 备注
 * @returns {Promise<number>} 新增后的节点 ID
 */
export const createNode = (data) => {
  return request({
    url: '/api/nodes',
    method: 'POST',
    data,
  })
}

/**
 * 编辑节点。
 *
 * @param {number|string} id 节点 ID
 * @param {Object} data
 * @param {string} data.title 节点名称
 * @param {string} [data.content] 节点说明
 * @param {string} [data.remark] 备注
 * @returns {Promise<boolean>}
 */
export const updateNode = (id, data) => {
  return request({
    url: `/api/nodes/${id}`,
    method: 'PUT',
    data,
  })
}
/**
 * 删除节点。
 *
 * @param {number|string} id 节点 ID
 * @param {Object} [data]
 * @param {string} [data.deleteMode] 删除模式：COMPLETE_TASKS / DELETE_TASKS
 * @returns {Promise<boolean>}
 */
export const deleteNode = (id, data) => {
  return request({
    url: `/api/nodes/${id}`,
    method: 'DELETE',
    data,
  })
}