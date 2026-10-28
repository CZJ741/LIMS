import request from '@/utils/request'

export interface CustomerItem {
  id?: string
  name: string
  legal_person?: string
  address?: string
  contact_person?: string
  mobile?: string
  phone?: string
  email?: string
  fax?: string
  tax_title?: string
  tax_number?: string
  invoice_address?: string
  invoice_phone?: string
  bank_name?: string
  bank_account?: string
  remarks?: string
  created_at?: string
  updated_at?: string
}

export interface CustomerQueryParams {
  name?: string
  address?: string
  contact_person?: string
  phone?: string
  page?: number
  page_size?: number
}

// 获取客户列表
export function getCustomers(params?: CustomerQueryParams) {
  return request.get('/contract/customers/', { params })
}

// 获取单个客户详情
export function getCustomerDetail(id: string) {
  return request.get(`/contract/customers/${id}/`)
}

// 新增客户
export function createCustomer(data: CustomerItem) {
  return request.post('/contract/customers/', data)
}

// 修改客户
export function updateCustomer(id: string, data: CustomerItem) {
  return request.put(`/contract/customers/${id}/`, data)
}

// 删除客户（软删除）
export function deleteCustomer(id: string) {
  return request.delete(`/contract/customers/${id}/`)
}

// 批量删除客户
export function batchDeleteCustomers(ids: string[]) {
  return request.post('/contract/customers/batch-delete/', { ids })
}
