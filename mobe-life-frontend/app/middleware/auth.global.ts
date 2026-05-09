/**
 * 做全局路由守卫：无 token 且非登录页时重定向到登录；已登录访问登录页则回首页。
 * 模块：前端基础设施 / 路由中间件。
 * 约束：服务端跳过（仅在浏览器执行）；与 `~/utils/auth` 中的 token 存储保持一致。
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
