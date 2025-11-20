import axios from 'axios'
import { useAuthStore } from '@/stores/auth'
import { ElMessage } from 'element-plus'

// 创建 axios 实例
const http = axios.create({
  baseURL: 'http://localhost:8080',
  timeout: 10000,
})

// 请求拦截器 - 自动添加 token
http.interceptors.request.use(
  (config) => {
    const authStore = useAuthStore()
    if (authStore.token) {
      // 从 store 中获取 token，添加到请求头
      config.headers.Authorization = `Bearer ${authStore.token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器 - 处理错误
http.interceptors.response.use(
  (response) => {
    const { code, message, data } = response.data

    // 如果后端返回 401，说明 token 过期
    if (code === 401) {
      const authStore = useAuthStore()
      authStore.logout()
      ElMessage.error('登录已过期，请重新登录')
      window.location.href = '/login'
      return Promise.reject(new Error(message))
    }

    // 如果返回错误码，就显示错误信息
    if (code !== 200 && code !== 0) {
      ElMessage.error(message || '请求失败')
      return Promise.reject(new Error(message))
    }

    // 正常返回，只返回 data 部分
    return data
  },
  (error) => {
    if (error.response) {
      ElMessage.error(error.response.data?.message || '请求失败')
    } else if (error.request) {
      ElMessage.error('网络错误，请检查连接')
    } else {
      ElMessage.error('请求出错')
    }
    return Promise.reject(error)
  }
)

export default http