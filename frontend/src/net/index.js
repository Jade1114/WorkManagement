// src/net/index.js
import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/userStore'
import router from '@/router'

const http = axios.create({
  baseURL: '/api',
  timeout: 8000
})

/* ----------------------------
   请求拦截器：自动注入 token
---------------------------- */
http.interceptors.request.use(
  config => {
    const userStore = useUserStore()
    if (userStore.token) {
      config.headers.Authorization = 'Bearer ' + userStore.token
    }
    return config
  },
  error => {
    ElMessage.error('请求发送失败')
    return Promise.reject(error)
  }
)

/* --------------------------------
   响应拦截器：统一处理 code 逻辑
---------------------------------- */
http.interceptors.response.use(
  response => {
    const res = response.data

    switch (res.code) {
      case 200:
        return res.data  // 成功，返回 data
      case 400:
        ElMessage.warning(res.message || '参数错误')
        break
      case 401:
        ElMessage.error('登录已过期，请重新登录')
        const userStore = useUserStore()
        userStore.logout()
        router.push('/login')
        break
      case 403:
        ElMessage.error('无权限执行此操作')
        break
      case 500:
        ElMessage.error('服务器错误，请稍后再试')
        break
      default:
        ElMessage.error(res.message || '未知错误')
    }

    return Promise.reject(res) // 让业务层的 catch 能捕获
  },

  error => {
    // HTTP 层面的错误
    ElMessage.error('网络连接失败，请检查网络')
    return Promise.reject(error)
  }
)

export default http 