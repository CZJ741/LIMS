import axios from 'axios'
import { ElMessage } from 'element-plus'

const service = axios.create({
  baseURL: '/api',
  timeout: 10000
})

/**
 * 递归深度提取后端返回的真实报错文本，严禁将具体业务错误掩盖为通用“网络请求错误”
 */
function extractErrorMessage(data: any): string {
  if (!data) return ''
  if (typeof data === 'string') return data
  if (data.message && typeof data.message === 'string') return data.message
  if (data.detail && typeof data.detail === 'string') return data.detail

  if (Array.isArray(data)) {
    for (const item of data) {
      const msg = extractErrorMessage(item)
      if (msg) return msg
    }
  } else if (typeof data === 'object') {
    const messages: string[] = []
    for (const key of Object.keys(data)) {
      const val = data[key]
      const msg = extractErrorMessage(val)
      if (msg) {
        messages.push(msg)
      }
    }
    if (messages.length > 0) {
      return messages.join('；')
    }
  }
  return ''
}

service.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('lims_token') || localStorage.getItem('access_token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

service.interceptors.response.use(
  (response) => {
    const res = response.data
    // 标准返回结构判断
    if (res.code && res.code !== 200 && res.code !== 201) {
      const errMsg = res.message || extractErrorMessage(res.data) || '系统业务处理异常'
      ElMessage.error(errMsg)
      return Promise.reject(new Error(errMsg))
    }
    return res
  },
  (error) => {
    let message = ''
    if (error.response?.data) {
      message = extractErrorMessage(error.response.data)
    }

    if (!message) {
      if (error.response?.status === 403) {
        message = '操作权限不足'
      } else if (error.response?.status === 404) {
        message = '请求的目标资源不存在'
      } else if (error.response?.status === 500) {
        message = '服务器内部异常，请联系管理员排查'
      } else if (error.message?.includes('Network Error')) {
        message = '后端服务连接失败，请确认后端服务已启动'
      } else {
        message = error.message || '请求处理异常'
      }
    }

    ElMessage.error(message)

    if (error.response?.status === 401) {
      localStorage.removeItem('lims_token')
      localStorage.removeItem('access_token')
      localStorage.removeItem('lims_user')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

export default service
