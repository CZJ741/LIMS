import axios from 'axios'
import { ElMessage } from 'element-plus'

const service = axios.create({
  baseURL: '/api',
  timeout: 10000
})

service.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('access_token')
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
    if (res.code && res.code !== 200) {
      ElMessage.error(res.message || '系统错误')
      return Promise.reject(new Error(res.message || 'Error'))
    }
    return res
  },
  (error) => {
    const message = error.response?.data?.message || error.response?.data?.detail || '网络请求错误'
    ElMessage.error(message)
    if (error.response?.status === 401) {
      localStorage.removeItem('access_token')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

export default service
