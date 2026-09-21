import { defineStore } from 'pinia'
import { ref } from 'vue'

export interface UserInfo {
  userId: string | number
  username: string
  realName: string
  roles: string[]
  permissions: string[]
}

export const useUserStore = defineStore('user', () => {
  const token = ref<string>(localStorage.getItem('lims_token') || '')
  const userInfo = ref<UserInfo | null>(null)

  function setToken(newToken: string) {
    token.value = newToken
    localStorage.setItem('lims_token', newToken)
  }

  function setUserInfo(info: UserInfo) {
    userInfo.value = info
  }

  function logout() {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('lims_token')
  }

  return {
    token,
    userInfo,
    setToken,
    setUserInfo,
    logout
  }
})
