import {
  clearAuth,
  getStoredUser,
  getToken,
  setStoredUser,
  setToken,
  type CurrentUser,
} from '~/utils/auth'

interface ApiResult<T> {
  code: number
  message: string
  data: T
}

interface CaptchaData {
  captchaKey: string
  captchaImage: string
}

interface LoginUserData {
  userId: number
  nickname?: string
  avatar?: string
  token: string
}

export const useAuth = () => {
  const { $api } = useNuxtApp()

  const token = useState<string>('auth_token', () => getToken())
  const currentUser = useState<CurrentUser | null>('current_user', () =>
    getStoredUser()
  )
  const loading = ref(false)

  const getCaptcha = async () => {
    const res = await $api<ApiResult<CaptchaData>>('/api/auth/captcha')
    return res.data
  }

  const passwordLogin = async (payload: {
    account: string
    password: string
    captchaKey: string
    captchaCode: string
  }) => {
    loading.value = true
    try {
      const res = await $api<ApiResult<LoginUserData>>(
        '/api/auth/password-login',
        {
          method: 'POST',
          body: payload,
        }
      )

      token.value = res.data.token
      setToken(res.data.token)

      await fetchCurrentUser()
      return res
    } finally {
      loading.value = false
    }
  }

  const sendLoginEmailCode = async (email: string) => {
    return await $api<ApiResult<boolean>>('/api/auth/send-login-email-code', {
      method: 'POST',
      body: { email },
    })
  }

  const codeLogin = async (payload: { account: string; code: string }) => {
    loading.value = true
    try {
      const res = await $api<ApiResult<LoginUserData>>('/api/auth/code-login', {
        method: 'POST',
        body: payload,
      })

      token.value = res.data.token
      setToken(res.data.token)

      await fetchCurrentUser()
      return res
    } finally {
      loading.value = false
    }
  }

  const fetchCurrentUser = async () => {
    const res = await $api<ApiResult<CurrentUser>>('/api/user/current')
    currentUser.value = res.data
    setStoredUser(res.data)
    return res.data
  }

  const logout = async () => {
    try {
      await $api('/api/auth/logout', {
        method: 'POST',
      })
    } catch {
      // ignore
    } finally {
      token.value = ''
      currentUser.value = null
      clearAuth()
      await navigateTo('/login')
    }
  }

  return {
    token,
    currentUser,
    loading,
    getCaptcha,
    passwordLogin,
    sendLoginEmailCode,
    codeLogin,
    fetchCurrentUser,
    logout,
  }
}
