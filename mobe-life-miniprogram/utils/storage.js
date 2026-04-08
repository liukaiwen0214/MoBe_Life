const TOKEN_KEY = 'MOBE_TOKEN'

const setToken = (token) => {
  wx.setStorageSync(TOKEN_KEY, token)
}

const getToken = () => {
  return wx.getStorageSync(TOKEN_KEY)
}

const removeToken = () => {
  wx.removeStorageSync(TOKEN_KEY)
}

export default {
  setToken,
  getToken,
  removeToken
}