import request from '../utils/request'

export const wxMiniLogin = (data) => {
  return request({
    url: '/api/auth/wx-mini-login',
    method: 'POST',
    data
  })
}

export const logout = () => {
  return request({
    url: '/api/auth/logout',
    method: 'POST'
  })
}