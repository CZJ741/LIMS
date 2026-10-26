import { defineStore } from 'pinia'
import { ref } from 'vue'

export interface UserInfo {
  username: string
  role: string
  realName: string
  token: string
}

export const useUserStore = defineStore('user', () => {
  const token = ref<string>(localStorage.getItem('lims_token') || '')
  const userInfo = ref<UserInfo | null>(
    localStorage.getItem('lims_user')
      ? JSON.parse(localStorage.getItem('lims_user')!)
      : null
  )

  const login = (info: UserInfo) => {
    token.value = info.token
    userInfo.value = info
    localStorage.setItem('lims_token', info.token)
    localStorage.setItem('lims_user', JSON.stringify(info))
  }

  const logout = () => {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('lims_token')
    localStorage.removeItem('lims_user')
  }

  const isAuthenticated = () => {
    return !!token.value
  }

  return {
    token,
    userInfo,
    login,
    logout,
    isAuthenticated
  }
})
