import axios, { type AxiosInstance, type AxiosResponse } from 'axios'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'

export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
  timestamp: number
}

// 创建 Axios 实例
const service: AxiosInstance = axios.create({
  baseURL: '/api',
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json;charset=utf-8'
  }
})

// 生成简单 UUID 作为幂等键
function generateUUID(): string {
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, (c) => {
    const r = (Math.random() * 16) | 0
    const v = c === 'x' ? r : (r & 0x3) | 0x8
    return v.toString(16)
  })
}

// 请求拦截器
service.interceptors.request.use(
  (config) => {
    const userStore = useUserStore()
    if (userStore.token) {
      config.headers['Authorization'] = `Bearer ${userStore.token}`
    }

    // 非 GET 请求自动附带 Idempotency-Key 幂等键防重
    if (config.method && config.method.toUpperCase() !== 'GET') {
      if (!config.headers['Idempotency-Key']) {
        config.headers['Idempotency-Key'] = generateUUID()
      }
    }

    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  (response: AxiosResponse<ApiResponse>) => {
    const res = response.data
    // 成功响应
    if (res.code === 200) {
      return res.data
    }

    // 401 登录态失效
    if (res.code === 401) {
      const userStore = useUserStore()
      userStore.logout()
      ElMessage.error(res.message || '登录已过期，请重新登录')
      window.location.href = '/login'
      return Promise.reject(new Error(res.message || 'Error'))
    }

    // 业务错误提示
    ElMessage.error(res.message || '系统繁忙，请稍后再试')
    return Promise.reject(new Error(res.message || 'Error'))
  },
  (error) => {
    let message = error.message
    if (error.response) {
      switch (error.response.status) {
        case 401:
          message = '未授权或登录已过期'
          useUserStore().logout()
          window.location.href = '/login'
          break
        case 403:
          message = '拒绝访问：您没有操作该资源的权限'
          break
        case 404:
          message = '请求的资源不存在'
          break
        case 500:
          message = '服务器内部错误'
          break
        default:
          message = `网络连接异常: ${error.response.status}`
      }
    }
    ElMessage.error(message)
    return Promise.reject(error)
  }
)

export default service
