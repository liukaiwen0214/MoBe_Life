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
