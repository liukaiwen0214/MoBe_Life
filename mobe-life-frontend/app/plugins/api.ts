import { getToken, clearAuth } from '~/utils/auth'

export default defineNuxtPlugin(() => {
  const config = useRuntimeConfig()

  const api = $fetch.create({
    baseURL: config.public.apiBase,
    onRequest({ options }) {
      const token = getToken()
      const headers = new Headers(options.headers || {})

      if (token) {
        headers.set('Authorization', `Bearer ${token}`)
      }

      options.headers = headers
    },
    onResponseError({ response }) {
      if (response.status === 401) {
        clearAuth()
        if (import.meta.client) {
          navigateTo('/login')
        }
      }
    },
  })

  return {
    provide: {
      api,
    },
  }
})
