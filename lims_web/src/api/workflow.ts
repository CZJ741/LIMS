import request from '@/utils/request'

export interface FlowActionLogItem {
  id: string
  action: string
  from_status: string
  to_status: string
  operator_name: string
  operator_role: string
  comment: string
  created_at_display: string
}

export function getWorkflowLogs(params: { app_label: string; model: string; object_id: string }) {
  return request<{ code: number; data: FlowActionLogItem[] }>({
    url: '/workflow/logs/',
    method: 'get',
    params
  })
}
