import request from '@/utils/request'
import type { RoleItem } from './role'

export interface DepartmentItem {
  id: string
  name: string
  code: string
  description?: string
}

export interface UserItem {
  id: number
  username: string
  user_code: string
  real_name: string
  phone: string
  email: string
  department: string | null
  department_name?: string
  roles?: string[]
  roles_info?: RoleItem[]
  company: string
  gender: string
  id_card: string
  employee_no: string
  birth_date: string | null
  job_title: string
  education: string
  graduated_school: string
  major: string
  work_start_date: string | null
  years_in_position: number | null
  training_experience: string
  work_experience: string
  residence_area: string
  is_active: boolean
  last_login?: string | null
  date_joined: string
}

export interface UserQueryParams {
  user_code?: string
  real_name?: string
  phone?: string
  email?: string
  department?: string
  company?: string
  page?: number
  page_size?: number
}

export function getUserList(params?: UserQueryParams) {
  return request<{
    code: number
    data: {
      count: number
      next: string | null
      previous: string | null
      results: UserItem[]
    } | UserItem[]
  }>({
    url: '/auth/users/',
    method: 'get',
    params
  })
}

export function createUser(data: Partial<UserItem>) {
  return request<{ code: number; message: string; data: UserItem }>({
    url: '/auth/users/',
    method: 'post',
    data
  })
}

export function updateUser(id: number, data: Partial<UserItem>) {
  return request<{ code: number; message: string; data: UserItem }>({
    url: `/auth/users/${id}/`,
    method: 'patch',
    data
  })
}

export function deleteUser(id: number) {
  return request<{ code: number; message: string }>({
    url: `/auth/users/${id}/`,
    method: 'delete'
  })
}

export function resetUserPassword(userId: number, newPassword?: string) {
  return request<{ code: number; message: string; data: { default_password?: string } }>({
    url: `/auth/users/${userId}/reset-password/`,
    method: 'post',
    data: newPassword ? { new_password: newPassword } : {}
  })
}

export function assignUserRoles(userId: number, roleIds: string[]) {
  return request<{ code: number; message: string; data: UserItem }>({
    url: `/auth/users/${userId}/assign-roles/`,
    method: 'post',
    data: { roles: roleIds }
  })
}

export function getDepartmentList() {
  return request<{ code: number; data: DepartmentItem[] }>({
    url: '/auth/departments/',
    method: 'get'
  })
}
