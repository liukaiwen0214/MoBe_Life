/**
 * 核心职责：封装个人资料与账号安全相关接口。
 * 所属业务模块：小程序业务层 / 资料与安全 API。
 */
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

export const sendEmailCode = (data) => {
  return request({
    url: '/api/auth/send-email-code',
    method: 'POST',
    data,
  })
}

export const bindEmail = (data) => {
  return request({
    url: '/api/auth/bind-email',
    method: 'POST',
    data,
  })
}

export const setPassword = (data) => {
  return request({
    url: '/api/auth/set-password',
    method: 'POST',
    data,
  })
}

export const changePassword = (data) => {
  return request({
    url: '/api/auth/change-password',
    method: 'POST',
    data,
  })
}
export const sendUnbindEmailCode = () => {
  return request({
    url: '/api/auth/send-unbind-email-code',
    method: 'POST',
  })
}

export const unbindEmail = (data) => {
  return request({
    url: '/api/user/unbind-email',
    method: 'POST',
    data,
  })
}
