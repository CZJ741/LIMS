import request from '@/utils/request'

export interface RoleItem {
  id: string
  name: string
  code: string
  sort_order: number
  description: string
  is_active?: boolean
  menu_permissions?: string[]
  created_at?: string
}

export function getRoleList() {
  return request<{ code: number; data: RoleItem[] }>({
    url: '/auth/roles/',
    method: 'get'
  })
}

export function createRole(data: Partial<RoleItem>) {
  return request<{ code: number; message: string; data: RoleItem }>({
    url: '/auth/roles/',
    method: 'post',
    data
  })
}

export function updateRole(id: string, data: Partial<RoleItem>) {
  return request<{ code: number; message: string; data: RoleItem }>({
    url: `/auth/roles/${id}/`,
    method: 'patch',
    data
  })
}

export function deleteRole(id: string) {
  return request<{ code: number; message: string }>({
    url: `/auth/roles/${id}/`,
    method: 'delete'
  })
}
