import { defineStore } from 'pinia'
import { ref } from 'vue'

export interface UserRole {
  id: string
  name: string
  code: string
  menu_permissions?: string[]
}

export interface UserInfo {
  username: string
  realName: string
  token: string
  roles: UserRole[]
  activeRoleId?: string
  activeRoleName?: string
  activeRoleCode?: string
  permissions: string[]
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

  const switchRole = (roleId: string) => {
    if (!userInfo.value || !userInfo.value.roles) return false
    const targetRole = userInfo.value.roles.find((r) => r.id === roleId)
    if (!targetRole) return false

    userInfo.value.activeRoleId = targetRole.id
    userInfo.value.activeRoleName = targetRole.name
    userInfo.value.activeRoleCode = targetRole.code

    // 若当前切换为超级管理员，拥有全部权限；否则赋予该职位专属的 menu_permissions
    if (targetRole.code === 'ADMIN' || userInfo.value.username === 'admin') {
      userInfo.value.permissions = [
        '/dashboard',
        '/system',
        '/system/permission',
        '/system/permission/org',
        '/system/permission/org/dept',
        '/system/permission/org/user-org',
        '/system/permission/org/role',
        '/system/permission/org/position',
        '/system/permission/user'
      ]
    } else {
      userInfo.value.permissions = targetRole.menu_permissions || ['/dashboard']
    }

    localStorage.setItem('lims_user', JSON.stringify(userInfo.value))
    return true
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
    switchRole,
    logout,
    isAuthenticated
  }
})
