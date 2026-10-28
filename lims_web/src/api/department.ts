import request from '@/utils/request'

export interface DepartmentItem {
  id: string
  name: string
  code: string
  parent: string | null
  parent_name?: string
  org_type: string
  org_type_display?: string
  business_type: string
  business_type_display?: string
  is_legal_person: boolean
  company_name: string
  sort_order: number
  is_active: boolean
  description?: string
  children?: DepartmentItem[]
  created_at?: string
}

export interface DepartmentParams {
  code?: string
  name?: string
  org_type?: string
  business_type?: string
  is_legal_person?: boolean | string
  is_active?: boolean | string
  as_tree?: boolean
}

export function getDepartmentList(params?: DepartmentParams) {
  return request<{ code: number; data: DepartmentItem[] }>({
    url: '/auth/departments/',
    method: 'get',
    params
  })
}

export function createDepartment(data: Partial<DepartmentItem>) {
  return request<{ code: number; message: string; data: DepartmentItem }>({
    url: '/auth/departments/',
    method: 'post',
    data
  })
}

export function updateDepartment(id: string, data: Partial<DepartmentItem>) {
  return request<{ code: number; message: string; data: DepartmentItem }>({
    url: `/auth/departments/${id}/`,
    method: 'put',
    data
  })
}

export function deleteDepartment(id: string) {
  return request<{ code: number; message: string }>({
    url: `/auth/departments/${id}/`,
    method: 'delete'
  })
}
