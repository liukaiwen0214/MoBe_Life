import request from '../utils/request'

export const updateUserProfile = (data) => {
  return request({
    url: '/api/user/profile/update',
    method: 'POST',
    data,
  })
}

export const bindPhone = (data) => {
  return request({
    url: '/api/auth/bind-phone',
    method: 'POST',
    data,
  })
}