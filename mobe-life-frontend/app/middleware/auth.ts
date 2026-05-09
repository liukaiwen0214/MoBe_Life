/**
 * 按路由声明的命名中间件执行与 `auth.global` 相同的登录态分流逻辑。
 * 模块：前端基础设施 / 路由中间件。
 * 约束：服务端跳过；供部分页面按需引用。
 */
import { getToken } from '~/utils/auth'

export default defineNuxtRouteMiddleware((to) => {
  if (import.meta.server) return

  const token = getToken()

  if (!token && to.path !== '/login') {
    return navigateTo('/login')
  }

  if (token && to.path === '/login') {
    return navigateTo('/')
  }
})
