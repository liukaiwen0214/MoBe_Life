import request from '../utils/request'

export const getCurrentUser = () => {
  return request({
    url: '/api/user/current',
    method: 'GET'
  })
}