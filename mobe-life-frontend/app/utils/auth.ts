/**
 * 封装浏览器侧登录凭证：token 与当前用户快照的读写。
 * 模块：前端基础设施 / 认证存储。
 * 约束：仅在客户端读写 localStorage；服务端渲染时不访问 DOM。
 */
const TOKEN_KEY = 'mobe_token'
const USER_KEY = 'mobe_user'

export interface CurrentUser {
  id: number
  openid?: string
  phone?: string
  email?: string
  nickname?: string
  avatar?: string
  gender?: number
  status?: number
  birthday?: string
  hasPassword?: boolean
  hasPhone?: boolean
  hasEmail?: boolean
}

export function getToken(): string {
  if (import.meta.client) {
    return localStorage.getItem(TOKEN_KEY) || ''
  }
  return ''
}

export function setToken(token: string) {
  if (import.meta.client) {
    localStorage.setItem(TOKEN_KEY, token)
  }
}

export function clearToken() {
  if (import.meta.client) {
    localStorage.removeItem(TOKEN_KEY)
  }
}

export function getStoredUser(): CurrentUser | null {
  if (!import.meta.client) return null

  const raw = localStorage.getItem(USER_KEY)
  if (!raw) return null

  try {
    return JSON.parse(raw)
  } catch {
    return null
  }
}

export function setStoredUser(user: CurrentUser) {
  if (import.meta.client) {
    localStorage.setItem(USER_KEY, JSON.stringify(user))
  }
}

export function clearStoredUser() {
  if (import.meta.client) {
    localStorage.removeItem(USER_KEY)
  }
}

export function clearAuth() {
  clearToken()
  clearStoredUser()
}
